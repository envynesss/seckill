package com.chou.dao;

import com.chou.entity.Seckill;
import com.chou.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

//@Repository
public class SuccessKilledDaoImpl implements SuccessKilledDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * 插入购买明细,可过滤重复
     * @param seckillId
     * @param userPhone
     * @return插入的行数
     */
    public int insertSuccessKilled(long seckillId,long userPhone){
        //<!--当出现主键冲突时(即重复秒杀时)，会报错;不想让程序报错，加入ignore-->
        String sql = "INSERT ignore INTO success_killed(seckill_id,user_phone,state) VALUES (?, ?, 0)";
        return jdbcTemplate.update(sql, new Object[]{seckillId, userPhone});
    }


    /**
     * 根据秒杀商品的id查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)
     * @param seckillId
     * @return
     */
    public SuccessKilled queryByIdWithSeckill(long seckillId, long userPhone){
        String sql = "SELECT\n" +
                "            sk.seckill_id,\n" +
                "            sk.user_phone,\n" +
                "            sk.create_time,\n" +
                "            sk.state,\n" +
                "            s.seckill_id \"seckill.seckill_id\",\n" +
                "            s.name \"seckill.name\",\n" +
                "            s.number \"seckill.number\",\n" +
                "            s.start_time \"seckill.start_time\",\n" +
                "            s.end_time \"seckill.end_time\",\n" +
                "            s.create_time \"seckill.create_time\"\n" +
                "        FROM success_killed sk\n" +
                "        INNER JOIN seckill s ON sk.seckill_id=s.seckill_id\n" +
                "        WHERE sk.seckill_id=?\n" +
                "        AND sk.user_phone=?";
        final SuccessKilled successKilled = new SuccessKilled();
        final Seckill seckill = new Seckill();
        jdbcTemplate.query(sql, new Object[]{seckillId, userPhone}, new RowCallbackHandler(){
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                seckill.setSeckillId(resultSet.getLong("seckill.seckill_id"));
                seckill.setName(resultSet.getString("seckill.name"));
                seckill.setNumber(resultSet.getInt("seckill.number"));
                seckill.setStartTime(resultSet.getDate("seckill.start_time"));
                seckill.setEndTime(resultSet.getDate("seckill.end_time"));
                seckill.setCreateTime(resultSet.getDate("seckill.create_time"));
                successKilled.setSeckillId(resultSet.getLong("seckill_id"));
                successKilled.setUserPhone(resultSet.getLong("user_phone"));
                successKilled.setState(resultSet.getShort("state"));
                successKilled.setCreateTime(resultSet.getDate("create_time"));
                successKilled.setSeckill(seckill);
            }
        });
        return successKilled;
    }
}
