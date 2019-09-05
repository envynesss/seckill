package com.chou.dao;

import com.chou.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

//@Repository
public class SeckillDaoImpl implements SeckillDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，表示更新库存的记录行数
     */
    @Override
    public int reduceNumber(long seckillId, Date killTime){
        String sql = "UPDATE seckill SET number = number - 1\n" +
                "        WHERE seckill_id = ?\n" +
                "        AND start_time <= ?\n" +
                "        AND end_time >= ?\n" +
                "        AND number > 0;" ;
        return jdbcTemplate.update(sql, new Object[]{seckillId, killTime, killTime});
    }

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
    public Seckill queryById(long seckillId){
        String sql = "SELECT * FROM seckill WHERE seckill_id=?";
        final Seckill seckill = new Seckill();
        jdbcTemplate.query(sql, new Object[]{seckillId}, new RowCallbackHandler(){
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                seckill.setSeckillId(resultSet.getLong("seckill_id"));
                seckill.setName(resultSet.getString("name"));
                seckill.setNumber(resultSet.getInt("number"));
                seckill.setStartTime(new Date(resultSet.getTimestamp("start_time").getTime()));
                seckill.setEndTime(new Date(resultSet.getTimestamp("end_time").getTime()));
                seckill.setCreateTime(new Date(resultSet.getTimestamp("create_time").getTime()));
            }
        });
        return seckill;
    }

    /**
     * 根据偏移量查询秒杀商品列表
     * @param off
     * @param limit
     * @return
     */
    public List<Seckill> queryAll(int off, int limit){
        String sql = "SELECT * FROM seckill ORDER BY create_time DESC limit ?, ?";
        List<Seckill> list = jdbcTemplate.query(sql, new Object[]{off, limit}, new BeanPropertyRowMapper<>(Seckill.class));
        return list;
    }
}
