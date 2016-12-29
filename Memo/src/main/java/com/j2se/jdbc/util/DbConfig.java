package com.j2se.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 类描述： 数据库配置文件读取方法
 * 
 * @Description:
 * @author xLw
 * @since JDK 1.7
 * @date 2016-4-19 下午1:53:55
 */
public class DbConfig {

	private String driver;
	private String url;
	private String userName;
	private String password;

	public DbConfig() {
		InputStream inputStream = DbConfig.class
				.getResourceAsStream("/jdbc.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
			this.driver = p.getProperty("driver");
			this.url = p.getProperty("url");
			this.userName = p.getProperty("username");
			this.password = p.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public static void main(String[] args) {
		new DbConfig();
	}

}