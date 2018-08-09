package com.zbensoft.e.payment.api.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.MessageDef.FIRST_LOGIN;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.service.api.MerchantDepartmentService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleRelService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantPositionService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.MerchantDepartment;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRole;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRoleRelKey;
import com.zbensoft.e.payment.db.domain.MerchantPosition;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.UserRsetPassword;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantEmployee")
@RestController
public class MerchantEmployeeController {

	private static final Logger log = LoggerFactory.getLogger(MerchantEmployeeController.class);

	@Autowired
	MerchantEmployeeService merchantEmployeeService;
	@Autowired
	MerchantUserService merchantUserService;
	@Autowired
	MerchantEmployeeRoleRelService merchantEmployeeRoleRelService;

	// 部门
	@Autowired
	MerchantDepartmentService merchantDepartmentService;

	// 职务
	@Autowired
	MerchantPositionService merchantPositionService;

	// 角色
	@Autowired
	MerchantEmployeeRoleService merchantEmployeeRoleService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@Value("${upload.img.tmp.folder}")
	private String TMP_FOLDER;
	@Value("${upload.img.rel.folder}")
	private String REL_FOLDER;
	@Value("${upload.img.read.folder}")
	private String READ_FOLDER;
	@Value("${upload.img.pre}")
	private String IMG_PRE;
	@Value("${upload.file.tmp.folder}")
	private String UPLOAD_FILE_FOLDER;

	@Value("${password.default}")
	private String DEFAULT_PASSWORD;
	@Value("${payPassword.default}")
	private String DEFAULT_PAYPASSWORD;

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query the merchantEmployee, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantEmployee>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userId, @RequestParam(required = false) String merchantDepartmentId,
			@RequestParam(required = false) String merchantPositionId, @RequestParam(required = false) String userName, @RequestParam(required = false) Integer status, @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String sellerIdNumber, @RequestParam(required = false) Integer isLocked, @RequestParam(required = false) Integer isFirstLogin, @RequestParam(required = false) String remark,
			@RequestParam(required = false) Integer emailBindStatus, @RequestParam(required = false) String email, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		sellerIdNumber = CommonFun.getRelVid(sellerIdNumber);

		// merchantEmployeeService.count();
		MerchantEmployee merchantEmployee = new MerchantEmployee();
		// 必须输入一个进行查询
		if ((idNumber == null || "".equals(idNumber)) && (id == null || "".equals(id)) && (sellerIdNumber == null || "".equals(sellerIdNumber)) && (userId == null || "".equals(userId))
				&& (userName == null || "".equals(userName))) {
			return new ResponseRestEntity<List<MerchantEmployee>>(new ArrayList<MerchantEmployee>(), HttpRestStatus.NOT_FOUND);
		}
		if (sellerIdNumber == null || "".equals(sellerIdNumber)) {
			merchantEmployee.setUserId(userId);
		} else {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(sellerIdNumber);
			if (merchantUser == null) {
				return new ResponseRestEntity<List<MerchantEmployee>>(new ArrayList<MerchantEmployee>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (userId == null || "".equals(userId)) {
					merchantEmployee.setUserId(merchantUser.getUserId());
				} else {
					if (userId.equals(merchantUser.getUserId())) {
						merchantEmployee.setUserId(userId);
					} else {
						return new ResponseRestEntity<List<MerchantEmployee>>(new ArrayList<MerchantEmployee>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}

		merchantEmployee.setIdNumber(idNumber);
		merchantEmployee.setEmployeeUserId(id);

		merchantEmployee.setMerchantDepartmentId(merchantDepartmentId);
		merchantEmployee.setMerchantPositionId(merchantPositionId);
		merchantEmployee.setUserName(userName);
		merchantEmployee.setStatus(status);
		merchantEmployee.setIsLocked(isLocked);

		merchantEmployee.setIsFirstLogin(isFirstLogin);
		merchantEmployee.setRemark(remark);
		merchantEmployee.setEmailBindStatus(emailBindStatus);
		merchantEmployee.setEmail(email);

		int count = merchantEmployeeService.count(merchantEmployee);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantEmployee>>(new ArrayList<MerchantEmployee>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantEmployee> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantEmployeeService.selectPage(merchantEmployee);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantEmployeeService.selectPage(merchantEmployee);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantEmployee>>(new ArrayList<MerchantEmployee>(), HttpRestStatus.NOT_FOUND);
		}

		List<MerchantEmployee> listNew = new ArrayList<MerchantEmployee>();
		MerchantEmployeeRole roleBean = new MerchantEmployeeRole();
		List<MerchantEmployeeRole> roleList = merchantEmployeeRoleService.selectPage(roleBean);
		for (MerchantEmployee bean : list) {
			MerchantDepartment merchantDepartment = merchantDepartmentService.selectByPrimaryKey(bean.getMerchantDepartmentId());
			MerchantPosition merchantPosition = merchantPositionService.selectByPrimaryKey(bean.getMerchantPositionId());
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(bean.getUserId());
			List<MerchantEmployeeRoleRelKey> relList = merchantEmployeeRoleRelService.selectByUserId(bean.getEmployeeUserId());
			if (merchantDepartment != null) {
				bean.setMerchantDepartmentName(merchantDepartment.getName());
			}
			if (merchantPosition != null) {
				bean.setMerchantPositionName(merchantPosition.getName());
			}
			if (merchantUser != null) {
				bean.setSellerIdNumber(merchantUser.getIdNumber());
			}
			String str = "";
			String roleStr = "";
			if (roleList != null && roleList.size() > 0) {
				for (MerchantEmployeeRole roles : roleList) {
					if (relList != null && relList.size() > 0) {
						for (MerchantEmployeeRoleRelKey keys : relList) {
							if (roles.getRoleId().equals(keys.getRoleId())) {
								str += roles.getName() + ",";
								roleStr += keys.getRoleId() + ",";
							}
						}
					}
				}
			}
			if (!"".equals(str) && str.length() > 0) {
				bean.setRoleName(str.substring(0, str.length() - 1));
			}

			if (!"".equals(roleStr) && roleStr.length() > 0) {
				bean.setRoleId(roleStr.substring(0, roleStr.length() - 1));
			}

			bean.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + bean.getIdNumber(), Calendar.getInstance().getTime()));
			// merchantUser2.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(merchantUser2.getUserId(), Calendar.getInstance().getTime()));

			bean.setPassword(null);
			bean.setEmail(CommonFun.getEmailWhithStar(bean.getEmail()));
			listNew.add(bean);
		}
		// //界面展示锁定信息
		// for (MerchantEmployee merchantEmployee1 : listNew) {
		// String names = MessageDef.USER_TYPE.SYS_STRING+"_"+merchantEmployee1.getUserName();
		// boolean flag = RedisUtil.islimit_ERROR_PASSWORD_COUNT(names, Calendar.getInstance().getTime());
		// merchantEmployee1.setLockedPassword(flag);
		//
		//
		// }
		return new ResponseRestEntity<List<MerchantEmployee>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询用户
	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query the merchantEmployee", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantEmployee> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		List<MerchantEmployeeRoleRelKey> list = merchantEmployeeRoleRelService.selectByUserId(id);

		String str = "";
		if (list != null && list.size() > 0) {
			for (MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey : list) {
				str += merchantEmployeeRoleRelKey.getRoleId() + ",";
			}
			merchantEmployee.setRoleId(str);
		}

		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployee.setPassword(null);
		merchantEmployee.setEmail(CommonFun.getEmailWhithStar(merchantEmployee.getEmail()));

		return new ResponseRestEntity<MerchantEmployee>(merchantEmployee, HttpRestStatus.OK);
	}

	// 新增用户
	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "New merchantEmployees", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantEmployee(@Valid @RequestBody MerchantEmployee merchantEmployee, BindingResult result, UriComponentsBuilder ucBuilder) {
		merchantEmployee.setSellerIdNumber(CommonFun.getRelVid(merchantEmployee.getSellerIdNumber()));
		int limitNumber = MessageDef.EMPLOYEE_LIMIT.LIMIT;
		MerchantUser merchantUser = merchantUserService.selectByIdNumber(merchantEmployee.getSellerIdNumber());
		if (merchantUser == null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.MERCHANT_NOT_FOUND);
		}
		List<MerchantEmployee> lists = merchantEmployeeService.selectByUserId(merchantUser.getUserId());
		if (lists.size() >= limitNumber) {
			return new ResponseRestEntity<Void>(HttpRestStatus.EMPLOYEE_CONFLICT, localeMessageSourceService.getMessage("common.employee.conflict.message"));
		}

		merchantEmployee.setUserId(merchantUser.getUserId());
		String employeeId = merchantEmployee.getUserId() + "#" + String.valueOf(System.currentTimeMillis());
		merchantEmployee.setEmployeeUserId(employeeId);
		merchantEmployee.setIdNumber(CommonFun.getRelVid(merchantEmployee.getIdNumber()));
		merchantEmployee.setCreateTime(PageHelperUtil.getCurrentDate());
		merchantEmployee.setStatus(MessageDef.STATUS.ENABLE_INT);
		merchantEmployee.setIsLocked(MessageDef.LOCKED.UNLOCKED);
		merchantEmployee.setIsFirstLogin(FIRST_LOGIN.FIRST);
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		if (merchantEmployeeService.isMerchantEmployeeExist(merchantEmployee)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		merchantEmployee.setPassword(encoder.encode(DEFAULT_PASSWORD));
		merchantEmployeeService.insert(merchantEmployee);
		// 新增日志
		merchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantEmployee, CommonLogImpl.MERCHANT);
		// 关系表新增Start
		if (merchantEmployee.getRoleId() != null) {
			String[] idStr = merchantEmployee.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
				merchantEmployeeRoleRelKey.setEmployeeUserId(merchantEmployee.getEmployeeUserId());
				merchantEmployeeRoleRelKey.setRoleId(idStr[i]);
				merchantEmployeeRoleRelService.insert(merchantEmployeeRoleRelKey);
			}
		}
		// 关系表新增End

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantEmployee/{id}").buildAndExpand(merchantEmployee.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户信息
	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Modify merchantEmployee information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> updateMerchantEmployee(@PathVariable("id") String id, @Valid @RequestBody MerchantEmployee merchantEmployee, BindingResult result) {

		merchantEmployee.setIdNumber(CommonFun.getRelVid(merchantEmployee.getIdNumber()));
		MerchantEmployee currentMerchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		List<MerchantEmployeeRoleRelKey> merchantEmployeeRoleRelList = merchantEmployeeRoleRelService.selectByUserId(id);

		if (currentMerchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentMerchantEmployee.setMerchantDepartmentId(merchantEmployee.getMerchantDepartmentId());
		currentMerchantEmployee.setMerchantPositionId(merchantEmployee.getMerchantPositionId());
		currentMerchantEmployee.setIdNumber(merchantEmployee.getIdNumber());
		currentMerchantEmployee.setUserName(merchantEmployee.getUserName());
		currentMerchantEmployee.setStatus(merchantEmployee.getStatus());
		currentMerchantEmployee.setIsLocked(merchantEmployee.getIsLocked());
		currentMerchantEmployee.setIsFirstLogin(merchantEmployee.getIsFirstLogin());
		currentMerchantEmployee.setCreateTime(merchantEmployee.getCreateTime());
		currentMerchantEmployee.setRemark(merchantEmployee.getRemark());
		currentMerchantEmployee.setEmail(merchantEmployee.getEmail());
		currentMerchantEmployee.setEmailBindStatus(merchantEmployee.getEmailBindStatus());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantEmployee>(currentMerchantEmployee, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantEmployeeService.updateByPrimaryKey(currentMerchantEmployee);
		// 修改日志
		currentMerchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantEmployee, CommonLogImpl.MERCHANT);
		// 关系表修改start(逻辑:先删除，然后增加)
		if (merchantEmployeeRoleRelList != null && merchantEmployeeRoleRelList.size() > 0) {
			for (MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey : merchantEmployeeRoleRelList) {
				merchantEmployeeRoleRelService.deleteByPrimaryKey(merchantEmployeeRoleRelKey);
			}
		}

		if (merchantEmployee.getRoleId() != null && !"".equals(merchantEmployee.getRoleId())) {
			String[] idStr = merchantEmployee.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
				merchantEmployeeRoleRelKey.setEmployeeUserId(merchantEmployee.getEmployeeUserId());
				merchantEmployeeRoleRelKey.setRoleId(idStr[i]);
				merchantEmployeeRoleRelService.insert(merchantEmployeeRoleRelKey);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)

		return new ResponseRestEntity<MerchantEmployee>(currentMerchantEmployee, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户信息
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Modify part of the merchantEmployee information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantEmployee> updateMerchantEmployeeSelective(@PathVariable("id") String id, @Valid @RequestBody MerchantEmployee merchantEmployee, BindingResult result) {

		merchantEmployee.setIdNumber(CommonFun.getRelVid(merchantEmployee.getIdNumber()));
		MerchantEmployee currentMerchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		List<MerchantEmployeeRoleRelKey> merchantEmployeeRoleRelList = merchantEmployeeRoleRelService.selectByUserId(id);
		if (currentMerchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}

		currentMerchantEmployee.setMerchantDepartmentId(merchantEmployee.getMerchantDepartmentId());
		currentMerchantEmployee.setMerchantPositionId(merchantEmployee.getMerchantPositionId());
		currentMerchantEmployee.setIdNumber(merchantEmployee.getIdNumber());
		currentMerchantEmployee.setUserName(merchantEmployee.getUserName());
		currentMerchantEmployee.setStatus(merchantEmployee.getStatus());
		currentMerchantEmployee.setIsFirstLogin(merchantEmployee.getIsFirstLogin());
		currentMerchantEmployee.setCreateTime(merchantEmployee.getCreateTime());
		currentMerchantEmployee.setRemark(merchantEmployee.getRemark());
		currentMerchantEmployee.setEmail(merchantEmployee.getEmail());
		currentMerchantEmployee.setEmailBindStatus(merchantEmployee.getEmailBindStatus());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantEmployee>(currentMerchantEmployee, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantEmployeeService.updateByPrimaryKeySelective(currentMerchantEmployee);
		// 修改日志
		currentMerchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantEmployee, CommonLogImpl.MERCHANT);
		// 关系表修改start(逻辑:先删除，然后增加)
		if (merchantEmployeeRoleRelList != null && merchantEmployeeRoleRelList.size() > 0) {
			for (MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey : merchantEmployeeRoleRelList) {
				merchantEmployeeRoleRelService.deleteByPrimaryKey(merchantEmployeeRoleRelKey);
			}
		}

		if (merchantEmployee.getRoleId() != null) {
			String[] idStr = merchantEmployee.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
				merchantEmployeeRoleRelKey.setEmployeeUserId(merchantEmployee.getEmployeeUserId());
				merchantEmployeeRoleRelKey.setRoleId(idStr[i]);
				merchantEmployeeRoleRelService.insert(merchantEmployeeRoleRelKey);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)

		return new ResponseRestEntity<MerchantEmployee>(currentMerchantEmployee, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 删除指定用户
	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Delete the specified merchantEmployee", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantEmployee> deleteMerchantEmployee(@PathVariable("id") String id) {

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		List<MerchantEmployeeRoleRelKey> list = merchantEmployeeRoleRelService.selectByUserId(id);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployeeService.deleteByPrimaryKey(id);
		// 删除日志开始
		MerchantEmployee merchant = new MerchantEmployee();
		merchant.setEmployeeUserId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant, CommonLogImpl.MERCHANT);
		// 删除日志结束
		// 关系表删除start
		if (list != null && list.size() > 0) {
			for (MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey : list) {
				merchantEmployeeRoleRelService.deleteByPrimaryKey(merchantEmployeeRoleRelKey);
			}
		}
		// 关系表删除end
		return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "enable the specified merchantEmployee", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> enableMerchantEmployee(@PathVariable("id") String id) {

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		merchantEmployee.setStatus(0);
		merchantEmployeeService.updateByPrimaryKey(merchantEmployee);
		// 修改日志
		merchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, merchantEmployee, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "enable the specified merchantEmployee", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> disableMerchantEmployee(@PathVariable("id") String id) {

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		merchantEmployee.setStatus(1);
		merchantEmployeeService.updateByPrimaryKey(merchantEmployee);
		// 修改日志
		merchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, merchantEmployee, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.OK);
	}

	// 用户解冻
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "unfrost the specified merchantEmployee", notes = "")
	@RequestMapping(value = "/unfrost/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> unfrostMerchantUser(@PathVariable("id") String id) {

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployee.setIsLocked(0);
		merchantEmployeeService.updateByPrimaryKey(merchantEmployee);
		// 修改日志
		merchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UNFROST, merchantEmployee, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.OK);
	}

	// 用户冻结
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "frost the specified merchantEmployee", notes = "")
	@RequestMapping(value = "/frost/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> frostMerchantUser(@PathVariable("id") String id) {

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployee.setIsLocked(1);
		merchantEmployeeService.updateByPrimaryKey(merchantEmployee);
		// 修改日志
		merchantEmployee.setPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_FROST, merchantEmployee, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.OK);
	}

	// 重置登录密码
	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Reset  password", notes = "")
	@RequestMapping(value = "/reset/password", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> resetConsumerPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		// 校验旧密码的正确性
		MerchantEmployee merchantEmployeeValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			merchantEmployeeValidate = merchantEmployeeService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == merchantEmployeeValidate) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (!encoder.matches(userRsetPassword.getPassword(), merchantEmployeeValidate.getPassword())) {
				return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.oldpassword.error"));
			}

			if (MessageDef.STATUS.ENABLE_INT != merchantEmployeeValidate.getStatus()) {
				return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}

		// 修改密码
		merchantEmployeeValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		merchantEmployeeValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.NOT_FIRST);
		merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployeeValidate);
		merchantEmployeeValidate.setPassword("");
		merchantEmployeeValidate.setEmail(CommonFun.getEmailWhithStar(merchantEmployeeValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, merchantEmployeeValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(merchantEmployeeValidate, HttpRestStatus.OK);
	}

	// 重置密码
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Reset  password to default", notes = "")
	@RequestMapping(value = "/reset/defaultPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> resetDefaultPassword(@PathVariable("id") String id) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		MerchantEmployee merchantEmployeeValidate = merchantEmployeeService.selectByPrimaryKey(id);
		if (null == merchantEmployeeValidate) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.user.notfound"));
		}
		merchantEmployeeValidate.setPassword(encoder.encode(DEFAULT_PASSWORD));
		merchantEmployeeValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployeeValidate);
		merchantEmployeeValidate.setPassword(null);
		merchantEmployeeValidate.setEmail(CommonFun.getEmailWhithStar(merchantEmployeeValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, merchantEmployeeValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(merchantEmployeeValidate, HttpRestStatus.OK);
	}

	// 用户还原
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Reset  password to default", notes = "")
	@RequestMapping(value = "/restore/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> restoreMerchantUser(@PathVariable("id") String id) {

		MerchantEmployee merchantEmployeeValidate = merchantEmployeeService.selectByPrimaryKey(id);
		if (null == merchantEmployeeValidate) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.user.notfound"));
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (merchantEmployeeValidate.getIdNumber() != null) {
			String pwd = CommonFun.generaPassword(merchantEmployeeValidate.getIdNumber(), 6);
			// 登陆密码
			merchantEmployeeValidate.setPassword(encoder.encode(pwd));
		} else {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.EMPLOYEE_VID_NOT_EXIST);
		}
		merchantEmployeeValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		merchantEmployeeValidate.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.UN_BIND);
		merchantEmployeeValidate.setEmail(null);
		merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployeeValidate);
		merchantEmployeeValidate.setPassword(null);

		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, merchantEmployeeValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployee>(merchantEmployeeValidate, HttpRestStatus.OK);
	}

	///////////////////////////////////////////////// uplaod///User//Img//Start//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "upload user picture", notes = "")
	@PostMapping("/upload/picture/{id}") // //new annotation since 4.3
	public ResponseRestEntity<Picture> singleFileUpload(@RequestParam("avatar_src") Object src, @RequestParam("avatar_data") String data, @RequestParam("avatar_file") MultipartFile file,
			RedirectAttributes redirectAttributes, @PathVariable("id") String id) {

		HttpHeaders headers = new HttpHeaders();
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return new ResponseRestEntity<Picture>(headers, HttpRestStatus.BAD_REQUEST, localeMessageSourceService.getMessage("common.upload.file"));
		}

		try {
			// {"x":182.46153846153845,"y":90.61538461538463,"height":1116,"width":1116,"rotate":0}
			int x = 0;
			int y = 0;
			int w = 0;
			int h = 0;
			String[] datas = data.split(",");

			File tmpPath = new File(TMP_FOLDER);
			if (!tmpPath.exists() && !tmpPath.isDirectory()) {
				tmpPath.mkdirs();
			}
			File realPath = new File(REL_FOLDER);
			if (!realPath.exists() && !realPath.isDirectory()) {
				realPath.mkdirs();
			}

			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(TMP_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);
			String userImgUrl = IMG_PRE + System.currentTimeMillis();
			String destPath = REL_FOLDER + MessageDef.UPLOAD_PATH.MERCHANT_EMPLOYEE;
			File destPathDir = new File(destPath);
			if (!destPathDir.exists() && !destPathDir.isDirectory()) {
				destPathDir.mkdirs();
			}

			destPath += "/" + userImgUrl;
			File srcFile = new File(TMP_FOLDER + file.getOriginalFilename());

			if (!(getPicFormate(srcFile).equalsIgnoreCase(MessageDef.IMAGETYPE.JPG) || getPicFormate(srcFile).equalsIgnoreCase(MessageDef.IMAGETYPE.JPEG))) {
				return new ResponseRestEntity<Picture>(headers, HttpRestStatus.IMAGE_UPLOAD_TYPE_ERROR, localeMessageSourceService.getMessage("common.img.upload.typeFailed"));
			}

			if (datas != null && datas.length == 5) {
				x = Double.valueOf(datas[0].substring(datas[0].indexOf(":") + 1)).intValue();
				y = Double.valueOf(datas[1].substring(datas[1].indexOf(":") + 1)).intValue();
				w = Double.valueOf(datas[2].substring(datas[2].indexOf(":") + 1)).intValue();
				h = Double.valueOf(datas[3].substring(datas[3].indexOf(":") + 1)).intValue();
				x = x < 0 ? 0 : x;
				y = y < 0 ? 0 : y;
				w = w < 0 ? 0 : w;
				h = h < 0 ? 0 : h;
				cutImage(srcFile, destPath, x, y, w, h);
			} else {
				Path path2 = Paths.get(destPath);
				Files.write(path2, bytes);
			}

			srcFile.delete();
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");

			MerchantEmployee existMerchantEmployee = merchantEmployeeService.selectByPrimaryKey(id);
			deleteOldImgFile(existMerchantEmployee);// 删除旧的头像图片
			MerchantEmployee merchantEmployee = new MerchantEmployee();
			merchantEmployee.setEmployeeUserId(id);
			merchantEmployee.setUserImgUrl(userImgUrl);
			merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployee);
			// 修改日志
			merchantEmployee.setPassword("");
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, merchantEmployee, CommonLogImpl.MERCHANT);
			Picture pic = new Picture();
			pic.setPicName(userImgUrl);

			return new ResponseRestEntity<Picture>(pic, headers, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.upload.success"));

		} catch (Exception e) {
			log.error("UploadTestController.singleFileUpload exception", e);
			return new ResponseRestEntity<Picture>(headers, HttpRestStatus.IMAGE_UPLOAD_FAILED_ERROR, localeMessageSourceService.getMessage("common.img.unload.fail"));
		}
	}

	private String getPicFormate(File file) {
		// create an image input stream from the specified file
		ImageInputStream iis = null;
		try {
			iis = ImageIO.createImageInputStream(file);
			// get all currently registered readers that recognize the image format
			Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			// get the first reader
			ImageReader reader = iter.next();
			return reader.getFormatName();
		} catch (IOException e) {
			log.error("reader exception", e);
		} finally {
			try {
				iis.close();
			} catch (IOException e) {
				log.error("close ImageIO exception", e);
			}
		}
		return MessageDef.IMAGETYPE.UNKNOW;

	}

	private boolean deleteOldImgFile(MerchantEmployee existMerchantEmployee) {
		if (existMerchantEmployee != null && existMerchantEmployee.getUserImgUrl() != null) {
			File userImgFile = new File(existMerchantEmployee.getUserImgUrl());
			if (userImgFile.exists() && userImgFile.isFile()) {
				if (userImgFile.delete()) {
					return true;
				} else {
					log.error("userImgFile: " + existMerchantEmployee.getUserImgUrl() + " delete failed with unknow error.");
				}
			}
		}
		return false;

	}

	private static void cutImage(File src, String dest, int x, int y, int w, int h) throws IOException {
		Iterator<?> iterator = ImageIO.getImageReadersByFormatName("jpg");
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		File destFile = new File(dest);
		ImageIO.write(bi, "jpg", destFile);
		iis.close();
		in.close();
	}

	class Picture {
		private String picName;

		public String getPicName() {
			return picName;
		}

		public void setPicName(String picName) {
			this.picName = picName;
		}
	}

	///////////////////////////////////////////////// uplaod///User//Img//End//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////// get///User//Img//Start//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@ApiOperation(value = "download user picture", notes = "")
	@RequestMapping(value = "/get/picture/path/{path}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getUserImagByPath(@PathVariable("path") String path) {
		if (StringUtils.isEmpty(path)) {
			return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
		}
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			headers.add("charset", "utf-8");
			// 设置下载文件名
			String fileName = path;
			fileName = URLEncoder.encode(fileName, "UTF-8");
			File readPath = new File(READ_FOLDER + MessageDef.UPLOAD_PATH.MERCHANT_EMPLOYEE);
			if (!readPath.exists() && !readPath.isDirectory()) {
				readPath.mkdir();
			}

			InputStreamResource resource = new InputStreamResource(new FileInputStream(READ_FOLDER + MessageDef.UPLOAD_PATH.MERCHANT_EMPLOYEE + "/" + path));
			return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("image/jpeg")).body(resource);
		} catch (Exception e) {
			log.error("", e);
		}

		return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
	}
	///////////////////////////////////////////////// get///User//Img//End//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// 绑定邮箱
	@PreAuthorize("hasRole('MERCHANT')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/bindEmail", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> bindEmail(@RequestParam("userId") String userId, @RequestParam("email") String email) {

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(email)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid and email not empty");
		}
		if (!CommonFun.isEmail(email)) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_FORMAT_ERROR, "email format error");
		}
		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(userId);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		}
		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_BIND_SUBJECT);
		String vCode = CommonFun.getVcode();
		String content = CommonFun.getBindEmailContent(merchantEmployee.getUserName(), vCode, email);
		RedisUtil.setEmailVCode(userId, vCode);
		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, subject, email, null, null, content);
			return new ResponseRestEntity<>(HttpRestStatus.OK);
		} catch (Exception e) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_SEND_ERROR, "send email is error,please try again later.");
		}
	}

	// 绑定邮箱验证验证码
	@PreAuthorize("hasRole('MERCHANT')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/validateBindEmailCode", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> validateBindEmailCode(@RequestParam("userId") String userId, @RequestParam("email") String email, @RequestParam("validateCode") String validateCode) {

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(email) || StringUtils.isEmpty(validateCode)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid validateCode and email not empty");
		}
		if (!CommonFun.isEmail(email)) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_FORMAT_ERROR, "email format error");
		}

		String rvalidateCode = RedisUtil.getEmailVCode(userId);
		if (!validateCode.equals(rvalidateCode)) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_VALIDATE_CODE_ERROR, "email validate code error");
		}
		MerchantEmployee merchantEmployee = new MerchantEmployee();
		merchantEmployee.setEmployeeUserId(userId);
		merchantEmployee.setEmail(email);
		merchantEmployee.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.BIND);
		merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployee);

		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 绑定邮箱,不再提醒
	@PreAuthorize("hasRole('MERCHANT')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/notRemind", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> notRemind(@RequestParam("userId") String userId) {

		if (StringUtils.isEmpty(userId)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid not empty");
		}

		MerchantEmployee merchantEmployee = new MerchantEmployee();
		merchantEmployee.setEmployeeUserId(userId);
		merchantEmployee.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.NOT_REMIND);
		merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployee);

		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 通过vid查询用户信息
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/vid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantEmployee> selectByVid(@RequestParam("vid") String vid) {
		vid = CommonFun.getRelVid(vid);
		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByIdNumber(vid);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.NOT_FOUND);
		}
		merchantEmployee.setPassword(null);
		// merchantEmployee.setPayPassword(null);

		// Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantEmployeeService, merchantEmployee.getUserId(), merchantEmployee.getBalance());
		// merchantEmployee.setBalance(balance);
		merchantEmployee.setEmail(CommonFun.getEmailWhithStar(merchantEmployee.getEmail()));

		merchantEmployee.setEmail(CommonFun.getEmailWhithStar(merchantEmployee.getEmail()));
		return new ResponseRestEntity<MerchantEmployee>(merchantEmployee, HttpRestStatus.OK);
	}

	// 找回密码
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/retrievePassword", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> retrievePassword(@RequestParam("userId") String userId, @RequestParam("email") String email, @RequestParam("passwordType") String passwordType) {

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(email) || StringUtils.isEmpty(passwordType)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid passwordType and email not empty");
		}
		if (!CommonFun.isEmail(email)) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_FORMAT_ERROR, "email format error");
		}

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(userId);
		if (merchantEmployee == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		}
		if (!email.equals(merchantEmployee.getEmail())) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_NOT_MATH_ERROR, "email not math error");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		MerchantEmployee merchantEmployeeTmp = new MerchantEmployee();
		merchantEmployeeTmp.setEmployeeUserId(merchantEmployee.getEmployeeUserId());

		String loginPassword = null;
		String payPassword = null;
		if (passwordType.contains(MessageDef.PASSWORD_TYPE.LOGIN_PASSORD)) {
			loginPassword = CommonFun.getLoginPassword();
			merchantEmployeeTmp.setPassword(encoder.encode(loginPassword));
			merchantEmployeeTmp.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);// .setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		}

		// if (passwordType.contains(MessageDef.PASSWORD_TYPE.PAY_PASSWORD)) {
		// payPassword = CommonFun.getPayPassword();
		// merchantEmployeeTmp.setPassword(encoder.encode(payPassword));
		// merchantEmployeeTmp.setIsDefaultPayPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		// }
		merchantEmployeeService.updateByPrimaryKeySelective(merchantEmployeeTmp);
		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_RETRIEVE_PASSWORD_SUBJECT);
		String content = CommonFun.getRetrievePassordContent(merchantEmployee.getUserName(), email, loginPassword, payPassword);
		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, subject, email, null, null, content);
			return new ResponseRestEntity<>(HttpRestStatus.OK);
		} catch (Exception e) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_SEND_ERROR, "send email is error,please try again later.");
		}
	}

	// 校验登录密码
	@PreAuthorize("hasRole('MERCHANT')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/validate/loginpassword", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployee> validateLoginpassword(@RequestParam("id") String id, @RequestParam("password") String password) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		MerchantEmployee merchantEmployeeValidate = null;
		if (id != null && !id.isEmpty()) {
			merchantEmployeeValidate = merchantEmployeeService.selectByPrimaryKey(id);
		}
		if (null == merchantEmployeeValidate) {
			return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (MessageDef.STATUS.ENABLE_INT != merchantEmployeeValidate.getStatus()) {
				return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}

			if (MessageDef.LOCKED.UNLOCKED != merchantEmployeeValidate.getIsLocked()) {
				return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("common.user.islocked"));
			}

			// 支付密码输入错误次数校验
			String username = MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantEmployeeValidate.getIdNumber();
			if (RedisUtil.islimit_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<MerchantEmployee>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日登录密码错误次数限制");
			}

			if (!encoder.matches(password, merchantEmployeeValidate.getPassword())) {
				int count = RedisUtil.increment_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
				MerchantEmployee merchantEmployee = new MerchantEmployee();
				merchantEmployee.setRemainingTimes(count);
				return new ResponseRestEntity<MerchantEmployee>(merchantEmployee, HttpRestStatus.PASSWORD_ERROR, "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
		}

		merchantEmployeeValidate.setEmail(CommonFun.getEmailWhithStar(merchantEmployeeValidate.getEmail()));
		return new ResponseRestEntity<MerchantEmployee>(merchantEmployeeValidate, HttpRestStatus.OK);
	}

	// 解锁登录密码
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockLoginPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockLoginPassowrd(@RequestParam(required = false) String userId, @RequestParam(required = false) String idNumber) {
		idNumber = CommonFun.getRelVid(idNumber);
		if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(idNumber)) {

			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}

		if (StringUtils.isNotEmpty(userId)) {
			List<MerchantEmployee> merchantEmployee = merchantEmployeeService.selectByUserId(userId);
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantEmployee.get(0).getIdNumber(), Calendar.getInstance().getTime());
		}

		if (StringUtils.isNotEmpty(idNumber)) {
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + idNumber, Calendar.getInstance().getTime());
		}
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 解锁支付密码
	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockPayPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockPayPassowrd(@RequestParam(required = false) String userId) {
		if (StringUtils.isEmpty(userId)) {

			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}
		RedisUtil.delete_ERROR_PAY_PASSWORD_COUNT(userId, Calendar.getInstance().getTime());
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}
}
