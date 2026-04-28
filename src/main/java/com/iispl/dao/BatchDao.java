package com.iispl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.model.Batch;
import com.iispl.util.Db;

public class BatchDAO {

    // ── CREATE BATCH (transactional) ────────────────────────────────────────
    public long create(String batchRef, List<Long> chequeIds) {
        String insertBatch = "INSERT INTO batches (batch_ref, status, created_at) "
                + "VALUES (?, 'PENDING', CURRENT_TIMESTAMP) RETURNING id";
        String linkCheque  = "INSERT INTO batch_cheques (batch_id, cheque_id) VALUES (?, ?)";
        String markBatched = "UPDATE cheques SET batched = true WHERE id = ?";

        Connection con = null;
        try {
            con = Db.getConnection();
            con.setAutoCommit(false);

            long batchId;
            try (PreparedStatement ps = con.prepareStatement(insertBatch)) {
                ps.setString(1, batchRef);
                ResultSet rs = ps.executeQuery();
                rs.next();
                batchId = rs.getLong(1);
            }

            try (PreparedStatement psLink = con.prepareStatement(linkCheque);
                 PreparedStatement psMark = con.prepareStatement(markBatched)) {

                for (Long cid : chequeIds) {
                    psLink.setLong(1, batchId);
                    psLink.setLong(2, cid);
                    psLink.addBatch();

                    psMark.setLong(1, cid);
                    psMark.addBatch();
                }
                psLink.executeBatch();
                psMark.executeBatch();
            }

            con.commit();
            return batchId;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        } finally {
            Db.close(con);
        }
        return -1;
    }

    // ── PENDING BATCHES (for NPCI) ──────────────────────────────────────────
    public List<Batch> findPending() {
        return findByStatus("PENDING");
    }

    // ── PROCESSED BATCHES (for Reports) ────────────────────────────────────
    public List<Batch> findProcessed() {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches WHERE status IN ('ACCEPTED','REJECTED') ORDER BY id DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── ALL BATCHES ─────────────────────────────────────────────────────────
    public List<Batch> findAll() {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches ORDER BY id DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── UPDATE STATUS ───────────────────────────────────────────────────────
    public void updateStatus(long id, String status) {
        String sql = "UPDATE batches SET status = ? WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean rejectAndReleaseCheques(long id) {
        String rejectBatch = "UPDATE batches SET status = 'REJECTED' WHERE id = ? AND status = 'PENDING'";
        String releaseCheques = "UPDATE cheques SET batched = false WHERE id IN "
                + "(SELECT cheque_id FROM batch_cheques WHERE batch_id = ?)";

        Connection con = null;
        try {
            con = Db.getConnection();
            con.setAutoCommit(false);

            int updated;
            try (PreparedStatement rejectPs = con.prepareStatement(rejectBatch)) {
                rejectPs.setLong(1, id);
                updated = rejectPs.executeUpdate();
            }

            if (updated == 0) {
                con.rollback();
                return false;
            }

            try (PreparedStatement releasePs = con.prepareStatement(releaseCheques)) {
                releasePs.setLong(1, id);
                releasePs.executeUpdate();
            }

            con.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            return false;
        } finally {
            Db.close(con);
        }
    }

    // ── FIND BY ID ──────────────────────────────────────────────────────────
    public Batch findById(long id) {
        String sql = "SELECT * FROM batches WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── HELPER ──────────────────────────────────────────────────────────────
    private List<Batch> findByStatus(String status) {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT * FROM batches WHERE status = ? ORDER BY id DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Batch map(ResultSet rs) throws Exception {
        Batch b = new Batch();
        b.setId(rs.getLong("id"));
        b.setBatchRef(rs.getString("batch_ref"));
        b.setStatus(rs.getString("status"));
        b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return b;
    }
}
