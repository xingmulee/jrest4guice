package org.jpa4guice.interceptor;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jpa4guice.transaction.EntityManagerFactoryHolder;
import org.jrest.core.guice.GuiceContext;

public class LocalTransactionInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		EntityManagerFactoryHolder emfH = GuiceContext.getInstance().getInstance(EntityManagerFactoryHolder.class);
		EntityManager entityManager = emfH.getEntityManager();
		
		final EntityTransaction transaction = entityManager.getTransaction();
		
		if(transaction.isActive()){
			return methodInvocation.proceed();
		}

		//开始一个新的事务
		transaction.begin();
		
		Object result = null;
		try {
			//执行被拦截的业务方法
			result = methodInvocation.proceed();
			//提交事务
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			//回滚当前事务
			transaction.rollback();
			throw e;
		}
		
		emfH.closeEntityManager();
		
		//返回业务方法的执行结果
		return result;
	}
}
