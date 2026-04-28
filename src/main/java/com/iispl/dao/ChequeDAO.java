package com.iispl.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iispl.model.Cheque;
import com.iispl.util.Db;

public class ChequeDAO {

    // ── INSERT ──────────────────────────────────────────────────────────────
    public long save(Cheque c) {
        String sql = "INSERT INTO cheques (payee_name, account_number, ifsc_code, amount, " +
                     "bank_name, cheque_number, cheque_date, status, batched) " +
                     "VALUES (?,?,?,?,?,?,?,?,?) RETURNING id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getPayeeName());
            ps.setString(2, c.getAccountNumber());
            ps.setString(3, c.getIfscCode());
            ps.setDouble(4, c.getAmount());
            ps.setString(5, c.getBankName());
            ps.setString(6, c.getChequeNumber());
            ps.setDate(7, Date.valueOf(c.getChequeDate()));
            ps.setString(8, "PENDING");
            ps.setBoolean(9, false);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ── ALL CHEQUES (for validate list) ────────────────────────────────────
    public List<Cheque> findAll() {
        List<Cheque> list = new ArrayList<>();
        String sql = "SELECT * FROM cheques ORDER BY id DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Cheque> findPendingForValidation() {
        List<Cheque> list = new ArrayList<>();
        String sql = "SELECT * FROM cheques WHERE status = 'PENDING' AND batched = false ORDER BY id DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── VALIDATED & NOT BATCHED (for create batch) ─────────────────────────
    public List<Cheque> findValidatedUnbatched() {
        List<Cheque> list = new ArrayList<>();
        String sql = "SELECT * FROM cheques WHERE status = 'VALIDATED' AND batched = false ORDER BY id DESC";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── FIND BY ID ──────────────────────────────────────────────────────────
    public Cheque findById(long id) {
        String sql = "SELECT * FROM cheques WHERE id = ?";
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

    // ── UPDATE STATUS ───────────────────────────────────────────────────────
    public void updateStatus(long id, String status) {
        String sql = "UPDATE cheques SET status = ? WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateStatusIfPending(long id, String status) {
        String sql = "UPDATE cheques SET status = ? WHERE id = ? AND status = 'PENDING' AND batched = false";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── MARK AS BATCHED ─────────────────────────────────────────────────────
    public void markBatched(long id) {
        String sql = "UPDATE cheques SET batched = true WHERE id = ?";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── FIND BY BATCH ID ────────────────────────────────────────────────────
    public List<Cheque> findByBatchId(long batchId) {
        List<Cheque> list = new ArrayList<>();
        String sql = "SELECT c.* FROM cheques c " +
                     "JOIN batch_cheques bc ON bc.cheque_id = c.id " +
                     "WHERE bc.batch_id = ? ORDER BY c.id";
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, batchId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── MAPPER ──────────────────────────────────────────────────────────────
    private Cheque map(ResultSet rs) throws Exception {
        Cheque c = new Cheque();
        c.setId(rs.getLong("id"));
        c.setPayeeName(rs.getString("payee_name"));
        c.setAccountNumber(rs.getString("account_number"));
        c.setIfscCode(rs.getString("ifsc_code"));
        c.setAmount(rs.getDouble("amount"));
        c.setBankName(rs.getString("bank_name"));
        c.setChequeNumber(rs.getString("cheque_number"));
        c.setChequeDate(rs.getDate("cheque_date").toLocalDate());
        c.setStatus(rs.getString("status"));
        c.setBatched(rs.getBoolean("batched"));
        return c;
    }
}