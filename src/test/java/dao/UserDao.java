package dao;

import bean.FBData;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * User: liji
 * Date: 18/1/10
 * Time: 下午4:06
 */
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insertFBData(FBData fb) {
        String sql = "insert into fb_user(NAME,PWD,USER_TYPE,CREATE_TIME,REDIRECT_URL) values(?,?,?,?,?)";
        this.jdbcTemplate.update(sql, new Object[]{fb.getName(), fb.getPassword(), fb.getType(), fb.getCreateTime(), fb.getRedirectUrl()});
    }

}
