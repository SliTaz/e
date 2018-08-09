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
import com.zbensoft.e.payment.api.service.api.GovRoleUserService;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.db.domain.GovRoleUserKey;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.domain.UserRsetPassword;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/govUser")
@RestController
public class GovUserController {
	@Autowired
	GovUserService govUserService;

	@Autowired
	GovRoleUserService govRoleUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_GOV_U_Q')")
	@ApiOperation(value = "Query the govUser, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GovUser>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userName, @RequestParam(required = false) Integer status,

			@RequestParam(required = false) Integer isFirstLogin, @RequestParam(required = false) String remark, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		// govUserService.count();
		GovUser govUser = new GovUser();
		govUser.setUserId(id);
		govUser.setUserName(userName);
		govUser.setStatus(status);

		govUser.setIsFirstLogin(isFirstLogin);
		govUser.setRemark(remark);

		int count = govUserService.count(govUser);
		if (count == 0) {
			return new ResponseRestEntity<List<GovUser>>(new ArrayList<GovUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<GovUser> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = govUserService.selectPage(govUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = govUserService.selectPage(govUser);
		}

		if (list == null || list.isEmpty()) {

			return new ResponseRestEntity<List<GovUser>>(new ArrayList<GovUser>(), HttpRestStatus.NOT_FOUND);
		}
		
		for (GovUser bean : list) {
			bean.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.GOV_STRING + RedisDef.DELIMITER.UNDERLINE + bean.getUserName(), Calendar.getInstance().getTime()));
		}
		return new ResponseRestEntity<List<GovUser>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户
	@PreAuthorize("hasRole('R_GOV_U_Q')")
	@ApiOperation(value = "Query the govUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<GovUser> selectByPrimaryKey(@PathVariable("id") String id) {
		GovUser govUser = govUserService.selectByPrimaryKey(id);
		if (govUser == null) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<GovUser>(govUser, HttpRestStatus.OK);
	}

	// 新增用户
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "New govUsers", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createGovUser(@Valid @RequestBody GovUser govUser, BindingResult result, UriComponentsBuilder ucBuilder) {

		govUser.setUserId(IDGenerate.generateGOV_USER_ID());
		govUser.setCreateTime(PageHelperUtil.getCurrentDate());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		if (govUserService.isGovUserExist(govUser)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		govUser.setPassword(encoder.encode(govUser.getPassword()));
		govUserService.insert(govUser);
		// 新增日志
		govUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, govUser, CommonLogImpl.GOV_USER);
		// 关系表新增Start
		if (govUser.getRoleId() != null) {
			String[] idStr = govUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
				govRoleUserKey.setUserId(govUser.getUserId());
				govRoleUserKey.setRoleId(idStr[i]);
				govRoleUserService.insert(govRoleUserKey);
			}
		}
		// 关系表新增End

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/govUser/{id}").buildAndExpand(govUser.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户信息
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Modify govUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUser> updateGovUser(@PathVariable("id") String id, @Valid @RequestBody GovUser govUser, BindingResult result) {

		GovUser currentGovUser = govUserService.selectByPrimaryKey(id);
		List<GovRoleUserKey> govRoleUserList = govRoleUserService.selectByUserId(id);

		if (currentGovUser == null) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentGovUser.setUserName(govUser.getUserName());
		currentGovUser.setStatus(govUser.getStatus());

		currentGovUser.setIsFirstLogin(govUser.getIsFirstLogin());
		currentGovUser.setCreateTime(govUser.getCreateTime());
		currentGovUser.setRemark(govUser.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<GovUser>(currentGovUser, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		govUserService.updateByPrimaryKey(currentGovUser);
		// 修改日志
		currentGovUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentGovUser, CommonLogImpl.GOV_USER);

		// 关系表修改start(逻辑:先删除，然后增加)
		if (govRoleUserList != null && govRoleUserList.size() > 0) {
			for (GovRoleUserKey govRoleUserKey : govRoleUserList) {
				govRoleUserService.deleteByPrimaryKey(govRoleUserKey);
			}
		}

		if (govUser.getRoleId() != null && !"".equals(govUser.getRoleId())) {
			String[] idStr = govUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				GovRoleUserKey govRoleUserKey = new GovRoleUserKey();
				govRoleUserKey.setUserId(govUser.getUserId());
				govRoleUserKey.setRoleId(idStr[i]);
				govRoleUserService.insert(govRoleUserKey);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)

		return new ResponseRestEntity<GovUser>(currentGovUser, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户信息
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Modify part of the govUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GovUser> updateGovUserSelective(@PathVariable("id") String id, @RequestBody GovUser govUser) {

		GovUser currentGovUser = govUserService.selectByPrimaryKey(id);

		if (currentGovUser == null) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND);
		}
		govUser.setUserId(id);
		govUserService.updateByPrimaryKeySelective(govUser);
		// 修改日志
		govUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, govUser, CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUser>(currentGovUser, HttpRestStatus.OK);
	}

	// 删除指定用户
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Delete the specified govUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovUser> deleteGovUser(@PathVariable("id") String id) {

		GovUser govUser = govUserService.selectByPrimaryKey(id);
		List<GovRoleUserKey> list = govRoleUserService.selectByUserId(id);
		if (govUser == null) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND);
		}

		govUserService.deleteByPrimaryKey(id);
		// 删除日志开始
		GovUser gov = new GovUser();
		gov.setUserId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, gov, CommonLogImpl.GOV_USER);
		// 删除日志结束
		// 关系表删除start
		if (list != null && list.size() > 0) {
			for (GovRoleUserKey govRoleUserKey : list) {
				govRoleUserService.deleteByPrimaryKey(govRoleUserKey);
			}
		}
		// 关系表删除end
		return new ResponseRestEntity<GovUser>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "enable the specified govUser", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUser> enableGovUser(@PathVariable("id") String id) {

		GovUser govUser = govUserService.selectByPrimaryKey(id);
		if (govUser == null) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		govUser.setStatus(0);
		govUserService.updateByPrimaryKey(govUser);
		// 修改日志
		govUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, govUser, CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUser>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "enable the specified govUser", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUser> disableGovUser(@PathVariable("id") String id) {

		GovUser govUser = govUserService.selectByPrimaryKey(id);
		if (govUser == null) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		govUser.setStatus(1);
		govUserService.updateByPrimaryKey(govUser);
		// 修改日志
		govUser.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, govUser, CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUser>(HttpRestStatus.OK);
	}

	// 重置登录密码
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Reset users password", notes = "")
	@RequestMapping(value = "/reset/password", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUser> resetConsumerPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		// 校验旧密码的正确性
		GovUser govUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			govUserValidate = govUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == govUserValidate) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (!encoder.matches(userRsetPassword.getPassword(), govUserValidate.getPassword())) {
				return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.oldpassword.error"));
			}

			if (MessageDef.STATUS.ENABLE_INT != govUserValidate.getStatus()) {
				return new ResponseRestEntity<GovUser>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}

		// 修改密码
		govUserValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		govUserService.updateByPrimaryKeySelective(govUserValidate);
		govUserValidate.setPassword("");
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, govUserValidate, CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUser>(govUserValidate, HttpRestStatus.OK);
	}

	// 重置密码
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "Reset users password to default", notes = "")
	@RequestMapping(value = "/reset/defaultPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUser> resetDefaultPassword(@PathVariable("id") String id, @Valid @RequestBody GovUser govUser) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		GovUser govUserValidate = govUserService.selectByPrimaryKey(id);
		if (null == govUserValidate) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		govUserValidate.setPassword(encoder.encode(govUser.getPassword()));
		govUserService.updateByPrimaryKeySelective(govUserValidate);
		govUserValidate.setPassword(null);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, govUserValidate, CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUser>(govUserValidate, HttpRestStatus.OK);
	}

	// 修改登录密码
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "modify users password", notes = "")
	@RequestMapping(value = "/modify/password", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUser> modifySysUserPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_SAME, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_NOT_SAME, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		if ("".equals(userRsetPassword.getNewPassword())) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_NOT_NULL, localeMessageSourceService.getMessage("common.password.notnull"));
		}

		// 校验旧密码的正确性
		GovUser sysUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			sysUserValidate = govUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == sysUserValidate) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_NOT_USER, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (!encoder.matches(userRsetPassword.getPassword(), sysUserValidate.getPassword())) {
				return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_OLD_ERROR, localeMessageSourceService.getMessage("common.oldpassword.error"));
			}

			if (MessageDef.STATUS.ENABLE_INT != sysUserValidate.getStatus()) {
				return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_USER_ENABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<GovUser>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}

		// 修改密码
		sysUserValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		govUserService.updateByPrimaryKeySelective(sysUserValidate);
		sysUserValidate.setPassword("");
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, sysUserValidate, CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUser>(sysUserValidate, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.passwordsuccess"));
	}

	// 解锁登录密码
	@PreAuthorize("hasRole('R_GOV_U_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockLoginPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockLoginPassowrd(@RequestParam(required = false) String userId, @RequestParam(required = false) String userName) {
		if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(userName)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}

		if (StringUtils.isNotEmpty(userId)) {
			GovUser govUser = govUserService.selectByPrimaryKey(userId);
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.GOV_STRING + RedisDef.DELIMITER.UNDERLINE  + govUser.getUserName(), Calendar.getInstance().getTime());
		}

		if (StringUtils.isNotEmpty(userName)) {
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.GOV_STRING + RedisDef.DELIMITER.UNDERLINE  + userName, Calendar.getInstance().getTime());
		}
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}
}
