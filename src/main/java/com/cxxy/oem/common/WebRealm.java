package com.cxxy.oem.common;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import com.cxxy.oem.db.DbConfig;
import com.cxxy.oem.db.DbRecord;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class WebRealm extends AuthorizingRealm {
	// 从数据库中获取安全数据
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(); // 授权信息
		String username = (String) principals.getPrimaryPrincipal(); // 将object强转为string
		Record user = new DbRecord(DbConfig.T_USER_ROLE)
			.whereEqualTo("username", username)
			.include("userId")
			.include("roleId")
			.queryFirst(); // 查询当前用户在数据库的记录
		info.addRole(user.getStr("roleNameEn"));// 将数据库中查询的角色放置到当前角色当中
		return info;
	} // 授权


	/**
	 * 1.doGetAuthenticationInfo 获取认证消息，如果数据库中没有数据，返回null,如果得到正确的用户名和密码，返回指定类型的对象
	 * 
	 * 2.AuthenticationInfo 可以使用SimpleAuthenticationInfo实现类，封装给正确的用户名和密码
	 * 
	 * 3.token参数，就是我们需要认证的token
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 1. 把 AuthenticationToken 转换为 UsernamePasswordToken
		UsernamePasswordToken uToken = (UsernamePasswordToken) token;

		// 2. 从 UsernamePasswordToken 中获取 username
		String username = uToken.getUsername();

		// 3. 调用Model层方法，从数据库中查询记录
		if (!StrKit.isBlank(username)) {
			Record user = new DbRecord(DbConfig.T_USER).whereEqualTo("username", username).queryFirst();
			if (user != null) {

				ByteSource credentialsSalt = ByteSource.Util.bytes(username);//加盐值 这里的参数要给个唯一的
               
				// 4. 如果找到对应记录，就构建 AuthenticationInfo 对象并返回
				return new SimpleAuthenticationInfo(username, user.getStr("password"),credentialsSalt,this.getName());
				// 进行相应用户名的密码比对，shiro内部自动帮我们完成 doCredentialsMatch(token,info)
			}
		}
		return null;
	} // 认证

}
