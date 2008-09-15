package org.jrest4guice.rest.sna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jrest4guice.cache.CacheProvider;
import org.jrest4guice.guice.GuiceContext;
import org.jrest4guice.security.SecurityContext;

import com.google.inject.Inject;

/**
 * 
 * @author <a href="mailto:zhangyouqun@gmail.com">cnoss (QQ:86895156)</a>
 * 
 */
public class SNASessionHelper {
	
	static Log log = LogFactory.getLog(SNASessionHelper.class);
	
	static final String REMOVE_ATTRIBUTE = "removeAttribute";
	static final String SET_ATTRIBUTE = "setAttribute";
	static final String GET_ATTRIBUTE = "getAttribute";
	static final String GET_SESSION = "getSession";

	/**
	 * 缓存提供者
	 */
	private CacheProvider cacheProvider;

	public SNASessionHelper(CacheProvider cacheManager) {
		this.cacheProvider = cacheManager;
	}

	/**
	 * 创建HttpServletRequest的包装对象
	 * @param hRequest
	 * @param snaSession
	 * @return
	 */
	public HttpServletRequest createRequestWrapper(
			final HttpServletRequest hRequest) {
		final HttpSession hSession = hRequest.getSession();
		return (HttpServletRequest) Proxy.newProxyInstance(
				HttpServletRequest.class.getClassLoader(),
				new Class[] { HttpServletRequest.class },
				new RequestInvocationHandler(hRequest,hSession));
	}

	/**
	 * 从cache中查询会话对象
	 * @param id
	 * @return
	 */
	public SNASession getSNASession(String id,HttpSession hSession) {
		Object value = cacheProvider.get(id);
		if (value == null) {
			return new SNASession();
		}

		SNASession result = (SNASession) value;
		this.try2SynchronizeHttpSession2SNASession(hSession, result);
		return result;
	}
	
	/*
	 * 处理当cache服务器重启后的会话同步
	 */
	public void try2SynchronizeHttpSession2SNASession(HttpSession hSession,SNASession snaSession){
		if(snaSession.isEmpty()){
			Enumeration names = hSession.getAttributeNames();
			String name;
			while(names.hasMoreElements()){
				name = names.nextElement().toString();
				snaSession.put(name, hSession.getAttribute(name));
			}
		}
	}


	/**
	 * 创建HttpSession的包装对象
	 * @param hSession
	 * @param snaSession
	 * @return
	 */
	private HttpSession createSessionWrapper(final HttpSession hSession) {
		return (HttpSession) Proxy.newProxyInstance(HttpSession.class
				.getClassLoader(), new Class[] { HttpSession.class },
				new SessionInvocationHandler(hSession));
	}
	
	class RequestInvocationHandler implements InvocationHandler{
		private HttpServletRequest hRequest;
		private HttpSession hSession;
		private HttpSession proxySession = null;

		public RequestInvocationHandler(HttpServletRequest hRequest,HttpSession hSession){
			this.hRequest = hRequest;
			this.hSession = hSession;
		}

		public Object invoke(Object proxy, Method method,
				Object[] args) throws Throwable {
			if (method.getName().equalsIgnoreCase(
					SNASessionHelper.GET_SESSION)) {
				if (proxySession == null) {
					proxySession = SNASessionHelper.this.createSessionWrapper(hSession);
				}
				return proxySession;
			}
			return method.invoke(hRequest, args);
		}
	}
	
	
	class SessionInvocationHandler implements InvocationHandler{
		private HttpSession hSession;
		private SNASession snaSession;
		
		@Inject
		private SecurityContext securityContext;
		
		public SessionInvocationHandler(HttpSession hSession){
			this.hSession = hSession;
		}
		public Object invoke(Object proxy, Method method,
				Object[] args) throws Throwable {
			
			this.snaSession = GuiceContext.getInstance().getBean(SNASession.class);
			
			String methodName = method.getName();
			if (methodName
					.equalsIgnoreCase(SNASessionHelper.SET_ATTRIBUTE)) {
				snaSession.put(args[0], args[1]);
				log.debug(SNASessionHelper.SET_ATTRIBUTE+"＝》"+args[0]+"="+args[1]);
			} else if (methodName
					.equalsIgnoreCase(SNASessionHelper.GET_ATTRIBUTE)) {
				Object value = method.invoke(hSession, args);
				if(value == null){
					value = snaSession.get(args[0]);
				}
				log.debug(SNASessionHelper.GET_ATTRIBUTE+"＝》"+args[0]+"'s value is "+value);
				return value;
			} else if (methodName
					.equalsIgnoreCase(SNASessionHelper.REMOVE_ATTRIBUTE)) {
				snaSession.remove(args[0]);
				log.debug(SNASessionHelper.REMOVE_ATTRIBUTE+"＝》"+args[0]);
			}
			return method.invoke(hSession, args);
		}
	}
}