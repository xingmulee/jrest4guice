package org.cnoss.cms.service;


import org.cnoss.cms.entity.SpecialComment;
import org.cnoss.cms.exception.ServiceException;


/**
 * 专题模块业务处理接口
 * 

 */
public interface SpecialCommentService {

	/**
	 * 创建专题回复信息
	 * 
	 * @param specialComment 专题
	 */
	public void createSpecialComment(SpecialComment specialComment) throws ServiceException ;
	
	/**
	 * 修改专题回复
	 * 
	 * @param specialComment 专题回复
	 */
	public void editSpecialComment(SpecialComment specialComment) throws ServiceException ;
	
	/**
	 * 删除专题回复
	 * 
	 * @param specialCommentId 专题编号
	 * @throws ServiceException
	 */
	public void deleteSpecialComment(int specialCommentId) throws ServiceException ;
	
	/**
	 * 取得专题回复
	 * 
	 * @param specialCommentId 专题回复编号
	 * @return SpecialComment 专题回复对象
	 * @throws ServiceException 
	 */
	public SpecialComment getSpecialComment(int specialCommentId) throws ServiceException ;
	
}
