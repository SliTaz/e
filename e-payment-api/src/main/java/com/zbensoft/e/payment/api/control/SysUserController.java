package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.api.service.api.SysUserService;
import com.zbensoft.e.payment.db.domain.SysRoleUserKey;
import com.zbensoft.e.payment.db.domain.SysUser;
import com.zbensoft.e.payment.db.domain.UserRsetPassword;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/sysUser")
@RestController
public class SysUserController {
	@Autowired
	SysUserService sysUserService;
	@Autowired
	SysRoleUserService sysRoleUserService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_MANAG_U_Q')")
	@ApiOperation(value = "Query the sysUser, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysUser>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userName, @RequestParam(required = false) Integer status,
			@RequestParam(required = false) String password,

			@RequestParam(required = false) Integer isFirstLogin, @RequestParam(required = false) String remark, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		// sysUserService.count();
		SysUser sysUser = new SysUser();
		sysUser.setUserId(id);
		sysUser.setUserName(userName);
		sysUser.setStatus(status);

		sysUser.setPassword(password);

		sysUser.setIsFirstLogin(isFirstLogin);
		sysUser.setRemark(remark);

		int count = sysUserService.count(sysUser);
		if (count == 0) {
			return new ResponseRestEntity<List<SysUser>>(new ArrayList<SysUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<SysUser> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysUserService.selectPage(sysUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = sysUserService.selectPage(sysUser);
		}

		if (list == null || list.isEmpty()) {

			return new ResponseRestEntity<List<SysUser>>(new ArrayList<SysUser>(), HttpRestStatus.NOT_FOUND);
		}
		
		for (SysUser bean : list) {
			bean.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.SYS_STRING + RedisDef.DELIMITER.UNDERLINE + bean.getUserName(), Calendar.getInstance().getTime()));
		}
		return new ResponseRestEntity<List<SysUser>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户
	@PreAuthorize("hasRole('R_MANAG_U_Q')")
	@ApiOperation(value = "Query the sysUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysUser> selectByPrimaryKey(@PathVariable("id") String id) {
		SysUser sysUser = sysUserService.selectByPrimaryKey(id);
		if (sysUser == null) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysUser>(sysUser, HttpRestStatus.OK);
	}

	// 新增用户
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "New sysUsers", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createSysUser(@Valid @RequestBody SysUser sysUser, BindingResult result, UriComponentsBuilder ucBuilder) {

		sysUser.setUserId(IDGenerate.generateSYS_USER_ID());
		sysUser.setCreateTime(PageHelperUtil.getCurrentDate());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		if (sysUserService.isUserExist(sysUser)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		sysUser.setPassword(encoder.encode(sysUser.getPassword()));
		sysUserService.insert(sysUser);
		// 新增日志
		sysUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, sysUser, CommonLogImpl.MANAGE_USER);
		// 关系表新增Start
		if (sysUser.getRoleId() != null) {
			String[] idStr = sysUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
				sysRoleUserKey.setUserId(sysUser.getUserId());
				sysRoleUserKey.setRoleId(idStr[i]);
				sysRoleUserService.insert(sysRoleUserKey);
			}
		}
		// 关系表新增End

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/sysUser/{id}").buildAndExpand(sysUser.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户信息
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Modify sysUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUser> updateSysUser(@PathVariable("id") String id, @Valid @RequestBody SysUser sysUser, BindingResult result) {

		SysUser currentSysUser = sysUserService.selectByPrimaryKey(id);
		List<SysRoleUserKey> roleUserKeysList = sysRoleUserService.selectByUserId(id);
		if (currentSysUser == null) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentSysUser.setUserName(sysUser.getUserName());
		currentSysUser.setStatus(sysUser.getStatus());

		currentSysUser.setIsFirstLogin(sysUser.getIsFirstLogin());
		currentSysUser.setCreateTime(sysUser.getCreateTime());
		currentSysUser.setRemark(sysUser.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SysUser>(currentSysUser, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		sysUserService.updateByPrimaryKey(currentSysUser);
		// 修改日志
		currentSysUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentSysUser, CommonLogImpl.MANAGE_USER);
		// 关系表修改start(逻辑:先删除，然后增加)
		if (roleUserKeysList != null && roleUserKeysList.size() > 0) {
			for (SysRoleUserKey gateway : roleUserKeysList) {
				sysRoleUserService.deleteByPrimaryKey(gateway);
			}
		}

		if (sysUser.getRoleId() != null && !"".equals(sysUser.getRoleId())) {
			String[] idStr = sysUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();
				sysRoleUserKey.setUserId(sysUser.getUserId());
				sysRoleUserKey.setRoleId(idStr[i]);
				sysRoleUserService.insert(sysRoleUserKey);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)
		return new ResponseRestEntity<SysUser>(currentSysUser, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户信息
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Modify part of the sysUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysUser> updateSysUserSelective(@PathVariable("id") String id, @RequestBody SysUser sysUser) {

		SysUser currentSysUser = sysUserService.selectByPrimaryKey(id);

		if (currentSysUser == null) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND);
		}
		sysUser.setUserId(id);
		sysUserService.updateByPrimaryKeySelective(sysUser);
		// 修改日志
		sysUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, sysUser, CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUser>(currentSysUser, HttpRestStatus.OK);
	}

	// 删除指定用户
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Delete the specified sysUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysUser> deleteSysUser(@PathVariable("id") String id) {

		SysUser sysUser = sysUserService.selectByPrimaryKey(id);
		List<SysRoleUserKey> list = sysRoleUserService.selectByUserId(id);
		if (sysUser == null) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND);
		}

		sysUserService.deleteByPrimaryKey(id);
		// 删除日志开始
		SysUser sys = new SysUser();
		sys.setUserId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, sys, CommonLogImpl.MANAGE_USER);
		// 删除日志结束
		// 关系表删除start
		if (list != null && list.size() > 0) {
			for (SysRoleUserKey sysRoleUserKey : list) {
				sysRoleUserService.deleteByPrimaryKey(sysRoleUserKey);
			}
		}
		// 关系表删除end
		return new ResponseRestEntity<SysUser>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "enable the specified sysUser", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUser> enableSysUser(@PathVariable("id") String id) {

		SysUser sysUser = sysUserService.selectByPrimaryKey(id);
		if (sysUser == null) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		sysUser.setStatus(0);
		sysUserService.updateByPrimaryKey(sysUser);
		// 修改日志
		sysUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, sysUser, CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUser>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "enable the specified sysUser", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUser> disableSysUser(@PathVariable("id") String id) {

		SysUser sysUser = sysUserService.selectByPrimaryKey(id);
		if (sysUser == null) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		sysUser.setStatus(1);
		sysUserService.updateByPrimaryKey(sysUser);
		// 修改日志
		sysUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, sysUser, CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUser>(HttpRestStatus.OK);
	}

	// 重置登录密码
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Reset users password", notes = "")
	@RequestMapping(value = "/reset/password", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUser> resetConsumerPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		// 校验旧密码的正确性
		SysUser sysUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			sysUserValidate = sysUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == sysUserValidate) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (!encoder.matches(userRsetPassword.getPassword(), sysUserValidate.getPassword())) {
				return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.oldpassword.error"));
			}

			if (MessageDef.STATUS.ENABLE_INT != sysUserValidate.getStatus()) {
				return new ResponseRestEntity<SysUser>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}

		// 修改密码
		sysUserValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		sysUserService.updateByPrimaryKeySelective(sysUserValidate);
		sysUserValidate.setPassword("");
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, sysUserValidate, CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUser>(sysUserValidate, HttpRestStatus.OK);
	}

	// 重置密码
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "Reset users password to default", notes = "")
	@RequestMapping(value = "/reset/defaultPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUser> resetDefaultPassword(@PathVariable("id") String id, @Valid @RequestBody SysUser sysUser) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		SysUser sysUserValidate = sysUserService.selectByPrimaryKey(id);
		if (null == sysUserValidate) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		sysUserValidate.setPassword(encoder.encode(sysUser.getPassword()));
		sysUserService.updateByPrimaryKeySelective(sysUserValidate);
		sysUserValidate.setPassword(null);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, sysUserValidate, CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUser>(sysUserValidate, HttpRestStatus.OK);
	}

	/**/
	// 修改登录密码
	//@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "modify users password", notes = "")
	@RequestMapping(value = "/modify/password", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUser> modifySysUserPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_SAME, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_NOT_SAME, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		if ("".equals(userRsetPassword.getNewPassword())) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_NOT_NULL, localeMessageSourceService.getMessage("common.password.notnull"));
		}
		// 校验旧密码的正确性
		SysUser sysUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			sysUserValidate = sysUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == sysUserValidate) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_NOT_USER, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (!encoder.matches(userRsetPassword.getPassword(), sysUserValidate.getPassword())) {
				return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_OLD_ERROR, localeMessageSourceService.getMessage("common.oldpassword.error"));
			}

			if (MessageDef.STATUS.ENABLE_INT != sysUserValidate.getStatus()) {
				return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_USER_ENABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<SysUser>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}

		// 修改密码
		sysUserValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		sysUserService.updateByPrimaryKeySelective(sysUserValidate);
		sysUserValidate.setPassword("");
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, sysUserValidate, CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUser>(sysUserValidate, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.passwordsuccess"));
	}

	// 解锁登录密码
	@PreAuthorize("hasRole('R_MANAG_U_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockLoginPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockLoginPassowrd(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName) {
		if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(userName)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}

		if (StringUtils.isNotEmpty(userId)) {
			SysUser sysUser = sysUserService.selectByPrimaryKey(userId);
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.SYS_STRING + RedisDef.DELIMITER.UNDERLINE + sysUser.getUserName(), Calendar.getInstance().getTime());
		}

		if (StringUtils.isNotEmpty(userName)) {
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.SYS_STRING + RedisDef.DELIMITER.UNDERLINE + userName, Calendar.getInstance().getTime());
		}
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}
}
