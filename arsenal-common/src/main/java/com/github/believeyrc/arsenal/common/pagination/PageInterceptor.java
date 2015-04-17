package com.github.believeyrc.arsenal.common.pagination;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

/**
 * mybatis分页拦截器
 * @author yangrucheng
 * @created 2015年4月17日 下午5:03:07
 * @since 1.0
 * @version 1.0
 *
 */
@Intercepts({ @Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class }) })
public class PageInterceptor implements Interceptor {

	private static String PAGE_MAPPER_ID = "pageMapperId";

	private static String PAGE_DATABASE_TYPE = "pageDatabaseType";

	private String pageMapperId;

	private String pageDatabaseType;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
		StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(statementHandler, "delegate");
		MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
		if (!"".equals(pageMapperId) && mappedStatement.getId().matches(pageMapperId)) {
			BoundSql boundSql = delegate.getBoundSql();
			Object parameterObject = boundSql.getParameterObject();
			Page<?> page = null;
			if (parameterObject instanceof Page<?>) {
				page = (Page<?>) parameterObject;
			} else if (parameterObject instanceof Map<?, ?>) {
				Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
				for (Map.Entry<?, ?> entry : parameterMap.entrySet()) {
					if (entry.getValue() instanceof Page<?>) {
						page = (Page<?>) entry.getValue();
						break;
					}

				}
			}
			if (page == null) {
				throw new NullPointerException("page parameter is null");
			}
			String sql = boundSql.getSql();
			Connection connection = (Connection) invocation.getArgs()[0];
			String countSql = getCountSql(sql);
			BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql,
					boundSql.getParameterMappings(), parameterObject);
			copyAdditionalParameters(countBoundSql, boundSql);
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject,
					countBoundSql);

			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				preparedStatement = connection.prepareStatement(countSql);
				parameterHandler.setParameters(preparedStatement);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					page.setTotalRecord(count);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (resultSet != null) {
						resultSet.close();
					}
					if (preparedStatement != null) {
						preparedStatement.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			String pageSql = getPageSql(page, sql);
			ReflectUtil.setField(boundSql, "sql", pageSql);
		}

		return invocation.proceed();
	}

	private void copyAdditionalParameters(BoundSql target, BoundSql source) {
		List<ParameterMapping> parameterMappings = source.getParameterMappings();
		if (parameterMappings != null) {
			for (ParameterMapping parameter : parameterMappings) {
				if (source.hasAdditionalParameter(parameter.getProperty())) {

					target.setAdditionalParameter(parameter.getProperty(),
							source.getAdditionalParameter(parameter.getProperty()));
				}
			}
		}

	}


	private String getMysqlPageSql(Page<?> page, StringBuffer sql) {
		int offset = (page.getPageNo() - 1) * page.getPageSize();
		sql.append(" limit ").append(offset).append(",").append(page.getPageSize());
		return sql.toString();
	}

	private String getPageSql(Page<?> page, String sql) {
		StringBuffer sqlBuffer = new StringBuffer(sql);
		if ("mysql".equalsIgnoreCase(pageDatabaseType)) {
			return getMysqlPageSql(page, sqlBuffer);
		}
		return sqlBuffer.toString();
	}

	private String getCountSql(String sql) {
		return "select count(1) from ( " + sql + " ) as cnt";
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		pageDatabaseType = properties.getProperty(PAGE_DATABASE_TYPE);
		pageMapperId = properties.getProperty(PAGE_MAPPER_ID);
		if (pageMapperId == null || "".equals(pageMapperId)) {
			pageMapperId = "";
		}

	}

	private static class ReflectUtil {

		private static Field getField(Object obj, String fieldName) {
			Field field = null;
			for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
				try {
					field = clazz.getDeclaredField(fieldName);
					break;
				} catch (Exception e) {
				}
			}
			return field;
		}

		public static void setField(Object obj, String fieldName, Object fieldValue) {
			Field field = ReflectUtil.getField(obj, fieldName);
			if (field != null) {
				try {
					if (!Modifier.isPublic(field.getModifiers())) {
						field.setAccessible(true);
					}
					field.set(obj, fieldValue);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}

			}
		}

		public static Object getFieldValue(Object obj, String fieldName) {
			Object fieldValue = null;
			Field field = ReflectUtil.getField(obj, fieldName);
			if (field != null) {
				try {
					if (!Modifier.isPublic(field.getModifiers())) {
						field.setAccessible(true);
					}
					fieldValue = field.get(obj);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			return fieldValue;
		}
	}

}
