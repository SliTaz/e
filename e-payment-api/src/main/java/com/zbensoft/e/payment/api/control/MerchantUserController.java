package com.zbensoft.e.payment.api.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantRoleUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.vo.merchant.QueryMerchantBalanceVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.ImportUtil;
import com.zbensoft.e.payment.db.domain.MerchantRoleUserKey;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.UserRsetPassword;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantUser")
@RestController
public class MerchantUserController {

	private static final Logger log = LoggerFactory.getLogger(MerchantUserController.class);

	@Autowired
	MerchantUserService merchantUserService;

	@Autowired
	MerchantRoleUserService merchantRoleUserService;

	@Autowired
	ConsumerUserService consumerUserService;
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
	@PreAuthorize("hasRole('R_SELLER_Q')")
	@ApiOperation(value = "Query the merchantUser, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantUser>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userName, @RequestParam(required = false) String phoneNumber,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) Integer isLocked, @RequestParam(required = false) Integer isBindBankCard, @RequestParam(required = false) String password,
			@RequestParam(required = false) Integer isFirstLogin, @RequestParam(required = false) String remark, @RequestParam(required = false) String idNumber, @RequestParam(required = false) String clapStoreNo,
			@RequestParam(required = false) Integer isActive, @RequestParam(required = false) Integer isDefaultPassword, @RequestParam(required = false) Integer isDefaultPayPassword,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		// merchantUserService.count();
		MerchantUser merchantUser = new MerchantUser();

		// 必须输入一个进行查询
		if ((id == null || "".equals(id)) && (phoneNumber == null || "".equals(phoneNumber)) && (idNumber == null || "".equals(idNumber)) && (clapStoreNo == null || "".equals(clapStoreNo))) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}

		merchantUser.setUserId(id);
		merchantUser.setUserName(userName);
		merchantUser.setPhoneNumber(phoneNumber);
		merchantUser.setStatus(status);
		merchantUser.setIsLocked(isLocked);
		merchantUser.setIsBindBankCard(isBindBankCard);
		merchantUser.setPassword(password);
		merchantUser.setIsFirstLogin(isFirstLogin);
		merchantUser.setRemark(remark);
		merchantUser.setIdNumber(idNumber);
		merchantUser.setClapStoreNo(clapStoreNo);
		merchantUser.setIsActive(isActive);
		merchantUser.setIsDefaultPassword(isDefaultPassword);
		merchantUser.setIsDefaultPayPassword(isDefaultPayPassword);

		int count = merchantUserService.count(merchantUser);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantUser> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantUserService.selectPage(merchantUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantUserService.selectPage(merchantUser);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}
		for (MerchantUser merchantUser2 : list) {
			merchantUser2.setPassword("");
			merchantUser2.setPayPassword("");

			Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, merchantUser2.getUserId(), merchantUser2.getBalance());
			merchantUser2.setBalance(balance);

			merchantUser2.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantUser2.getIdNumber(), Calendar.getInstance().getTime()));
			merchantUser2.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(merchantUser2.getUserId(), Calendar.getInstance().getTime()));

			merchantUser2.setEmail(CommonFun.getEmailWhithStar(merchantUser2.getEmail()));
		}
		return new ResponseRestEntity<List<MerchantUser>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_SELLER_Q')")
	@ApiOperation(value = "Query the merchantUser, support paging", notes = "")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantUser>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userName, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		MerchantUser merchantUser = new MerchantUser();
		merchantUser.setUserId(id);
		merchantUser.setUserName(userName);

		int count = merchantUserService.count(merchantUser);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantUser> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantUserService.selectPage(merchantUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantUserService.selectPage(merchantUser);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}
		for (MerchantUser merchantUser2 : list) {
			merchantUser2.setPassword("");
			merchantUser2.setPayPassword("");

			Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, merchantUser2.getUserId(), merchantUser2.getBalance());
			merchantUser2.setBalance(balance);

			merchantUser2.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantUser2.getIdNumber(), Calendar.getInstance().getTime()));
			merchantUser2.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(merchantUser2.getUserId(), Calendar.getInstance().getTime()));

			merchantUser2.setEmail(CommonFun.getEmailWhithStar(merchantUser2.getEmail()));
		}
		return new ResponseRestEntity<List<MerchantUser>>(list, HttpRestStatus.OK, count, count);
	}

	/**
	 * 查询商户，将密码清空传到前台
	 * 
	 * @param id
	 * @return
	 */
	@PreAuthorize("hasRole('R_SELLER_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query the merchantUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantUser> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, merchantUser.getUserId(), merchantUser.getBalance());
		merchantUser.setBalance(balance);
		merchantUser.setEmail(CommonFun.getEmailWhithStar(merchantUser.getEmail()));

		return new ResponseRestEntity<MerchantUser>(merchantUser, HttpRestStatus.OK);
	}

	/**
	 * 根据ClapStoreNo查询商户
	 * 
	 * @param clapStoreNo
	 * @return
	 */
	@PreAuthorize("hasRole('R_SELLER_Q')")
	@ApiOperation(value = "Query the merchantUser", notes = "")
	@RequestMapping(value = "/clapStoreNo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantUser> selectByClapStoreNo(@PathVariable("id") String clapStoreNo) {
		MerchantUser merchantUser = merchantUserService.selectByClapId(clapStoreNo);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, merchantUser.getUserId(), merchantUser.getBalance());
		merchantUser.setBalance(balance);
		merchantUser.setEmail(CommonFun.getEmailWhithStar(merchantUser.getEmail()));

		return new ResponseRestEntity<MerchantUser>(merchantUser, HttpRestStatus.OK);
	}

	// 新增用户
	@PreAuthorize("hasRole('R_SELLER_Q')")
	@ApiOperation(value = "New merchantUsers", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantUser(@Valid @RequestBody MerchantUser merchantUser, BindingResult result, UriComponentsBuilder ucBuilder) {

		int limitNumber = MessageDef.EMPLOYEE_LIMIT.LIMIT;
		merchantUser.setUserId(IDGenerate.generateMERCHANT_USER_ID());
		merchantUser.setIdNumber(CommonFun.getRelVid(merchantUser.getIdNumber()));
		merchantUser.setCreateTime(PageHelperUtil.getCurrentDate());
		merchantUser.setBalance((double) 0);
		merchantUser.setEmployeeLimit(limitNumber);
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		if (merchantUserService.isMerchantUserExist(merchantUser)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		merchantUser.setPassword(encoder.encode(merchantUser.getPassword()));
		merchantUser.setPayPassword(encoder.encode(merchantUser.getPayPassword()));
		/*
		 * if(merchantUser.getIdNumber()!=null){ String number= merchantUser.getIdNumber().replaceAll("[^0-9]+","");
		 * 
		 * 
		 * String pwd = PageHelperUtil.generaPassword(number, 6); merchantUser.setPassword(encoder.encode(pwd)); merchantUser.setPayPassword(encoder.encode(pwd)); }else{
		 * merchantUser.setPassword(encoder.encode(DEFAULT_PASSWORD)); merchantUser.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD)); }
		 */
		merchantUserService.insert(merchantUser);
		// 新增日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantUser, CommonLogImpl.MERCHANT);
		// 关系表新增Start
		if (merchantUser.getRoleId() != null) {
			String[] idStr = merchantUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
				merchantRoleUserKey.setUserId(merchantUser.getUserId());
				merchantRoleUserKey.setRoleId(idStr[i]);
				merchantRoleUserService.insert(merchantRoleUserKey);
			}
		}
		// 关系表新增End

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantUser/{id}").buildAndExpand(merchantUser.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户信息
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "Modify merchantUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> updateMerchantUser(@PathVariable("id") String id, @Valid @RequestBody MerchantUser merchantUser, BindingResult result) {

		merchantUser.setIdNumber(CommonFun.getRelVid(merchantUser.getIdNumber()));
		MerchantUser currentMerchantUser = merchantUserService.selectByPrimaryKey(id);
		List<MerchantRoleUserKey> merchantRoleUserList = merchantRoleUserService.selectByUserId(id);
		if (currentMerchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMerchantUser.setUserName(merchantUser.getUserName());
		currentMerchantUser.setPhoneNumber(merchantUser.getPhoneNumber());
		currentMerchantUser.setStatus(merchantUser.getStatus());
		currentMerchantUser.setIsLocked(merchantUser.getIsLocked());
		currentMerchantUser.setIsBindBankCard(merchantUser.getIsBindBankCard());
		currentMerchantUser.setIsFirstLogin(merchantUser.getIsFirstLogin());
		currentMerchantUser.setCreateTime(merchantUser.getCreateTime());
		currentMerchantUser.setRemark(merchantUser.getRemark());
		currentMerchantUser.setIdNumber(CommonFun.getRelVid(merchantUser.getIdNumber()));
		currentMerchantUser.setClapStoreNo(merchantUser.getClapStoreNo());
		currentMerchantUser.setIsActive(merchantUser.getIsActive());
		currentMerchantUser.setIsDefaultPassword(merchantUser.getIsDefaultPassword());
		currentMerchantUser.setIsDefaultPayPassword(merchantUser.getIsDefaultPayPassword());
		currentMerchantUser.setEmail(merchantUser.getEmail());
		currentMerchantUser.setEmailBindStatus(merchantUser.getEmailBindStatus());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantUser>(currentMerchantUser, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantUserService.updateByPrimaryKey(currentMerchantUser);
		// 修改日志
		currentMerchantUser.setPassword("");
		currentMerchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantUser, CommonLogImpl.MERCHANT);
		// 关系表修改start(逻辑:先删除，然后增加)
		if (merchantRoleUserList != null && merchantRoleUserList.size() > 0) {
			for (MerchantRoleUserKey merchantRoleUserKey : merchantRoleUserList) {
				merchantRoleUserService.deleteByPrimaryKey(merchantRoleUserKey);
			}
		}

		if (merchantUser.getRoleId() != null && !"".equals(merchantUser.getRoleId())) {
			String[] idStr = merchantUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
				merchantRoleUserKey.setUserId(merchantUser.getUserId());
				merchantRoleUserKey.setRoleId(idStr[i]);
				merchantRoleUserService.insert(merchantRoleUserKey);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)

		return new ResponseRestEntity<MerchantUser>(currentMerchantUser, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户信息
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "Modify part of the merchantUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantUser> updateMerchantUserSelective(@PathVariable("id") String id, @RequestBody MerchantUser merchantUser) {

		MerchantUser currentMerchantUser = merchantUserService.selectByPrimaryKey(id);

		if (currentMerchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		merchantUser.setUserId(id);
		merchantUserService.updateByPrimaryKeySelective(merchantUser);
		// 修改日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantUser, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(currentMerchantUser, HttpRestStatus.OK);
	}

	// 删除指定用户
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "Delete the specified merchantUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantUser> deleteMerchantUser(@PathVariable("id") String id) {

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		List<MerchantRoleUserKey> list = merchantRoleUserService.selectByUserId(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}

		merchantUserService.deleteByPrimaryKey(id);
		// 删除日志开始
		MerchantUser merchant = new MerchantUser();
		merchant.setUserId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant, CommonLogImpl.MERCHANT);
		// 删除日志结束
		// 关系表删除start
		if (list != null && list.size() > 0) {
			for (MerchantRoleUserKey merchantRoleUserKey : list) {
				merchantRoleUserService.deleteByPrimaryKey(merchantRoleUserKey);
			}
		}
		// 关系表删除end
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NO_CONTENT);
	}

	// 批量
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "update Many merchantUser", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> deleteSysLogMany(@PathVariable("id") String id) {
		String[] idStr = id.split(",");
		if (idStr != null) {
			for (String str : idStr) {
				MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(str);
				if (merchantUser == null) {
					return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
				}
				// 改变用户状态 0:启用 1:停用
				merchantUser.setIsActive(1);
				merchantUserService.updateByPrimaryKey(merchantUser);

				/*
				 * //修改日志 merchantUser.setPassword(""); merchantUser.setPayPassword(""); CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE,
				 * merchantUser,CommonLogImpl.MERCHANT);
				 */
			}
		}
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "enable the specified merchantUser", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> enableMerchantUser(@PathVariable("id") String id) {

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		merchantUser.setStatus(0);
		merchantUserService.updateByPrimaryKey(merchantUser);
		// 修改日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, merchantUser, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "enable the specified merchantUser", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> disableMerchantUser(@PathVariable("id") String id) {

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		merchantUser.setStatus(1);
		merchantUserService.updateByPrimaryKey(merchantUser);
		// 修改日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, merchantUser, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 用户解冻
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "unfrost the specified merchantUser", notes = "")
	@RequestMapping(value = "/unfrost/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> unfrostMerchantUser(@PathVariable("id") String id) {

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}

		merchantUser.setIsLocked(0);
		merchantUserService.updateByPrimaryKey(merchantUser);
		// 修改日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UNFROST, merchantUser, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 用户冻结
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "frost the specified merchantUser", notes = "")
	@RequestMapping(value = "/frost/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> frostMerchantUser(@PathVariable("id") String id) {

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}

		merchantUser.setIsLocked(1);
		merchantUserService.updateByPrimaryKey(merchantUser);
		// 修改日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_FROST, merchantUser, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 用户还原
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "frost the specified merchantUser", notes = "")
	@RequestMapping(value = "/restore/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> restoreMerchantUser(@PathVariable("id") String id) {

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(id);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (merchantUser.getClapStoreNo() != null && merchantUser.getClapStoreNo().split("-").length > 0) {
			String[] clapCodes = merchantUser.getClapStoreNo().split("-");
			String pwd = CommonFun.generaPassword(clapCodes[clapCodes.length - 1], 6);
			String enPassword = encoder.encode(pwd);
			// 登陆密码
			merchantUser.setPassword(enPassword);
			// 支付密码
			merchantUser.setPayPassword(enPassword);

		} else {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.CLAP_STORE_NO_NOT_EXIST);
		}

		merchantUser.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		merchantUser.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		merchantUser.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.DEFUALT);
		merchantUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.UN_BIND);
		merchantUser.setEmail(null);
		merchantUserService.updateByPrimaryKey(merchantUser);
		// 修改日志
		merchantUser.setPassword("");
		merchantUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_FROST, merchantUser, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 时间激活
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "activate the merchantUser by time", notes = "")
	@RequestMapping(value = "/activateMerchantUserBytime", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> activateMerchantBytime(@RequestBody MerchantUser merchantUser, BindingResult result) {
		merchantUser.setIsActive(MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE);

		// 校验空
		if ((merchantUser.getTimeStart() == null || "".equals(merchantUser.getTimeStart())) && (merchantUser.getTimeEnd() == null || "".equals(merchantUser.getTimeEnd()))) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.CONSUMER_ACTIVATE_TIME_NOTEMPTY);
		}

		// 校验开始时间大于结束时间
		if ((merchantUser.getTimeStart() != null && !"".equals(merchantUser.getTimeStart())) && (merchantUser.getTimeEnd() != null && !"".equals(merchantUser.getTimeEnd()))) {

			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startDate = df.parse(merchantUser.getTimeStart());
				Date endDate = df.parse(merchantUser.getTimeEnd());
				if (startDate.getTime() > endDate.getTime()) {
					return new ResponseRestEntity<MerchantUser>(HttpRestStatus.STARTTIME_OVER_ENDTIME_ERROR);
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}

		merchantUserService.activateByTime(merchantUser);
		return new ResponseRestEntity<MerchantUser>(HttpRestStatus.OK);
	}

	// 重置登录密码
	@PreAuthorize("hasRole('R_SELLER_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Reset users password", notes = "")
	@RequestMapping(value = "/reset/password", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> resetConsumerPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_SAME, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_NOT_SAME, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		// 校验旧密码的正确性
		MerchantUser merchantUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			merchantUserValidate = merchantUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == merchantUserValidate) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_NOT_NULL, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {

			if (MessageDef.STATUS.ENABLE_INT != merchantUserValidate.getStatus()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_OLD_ERROR, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
			if (MessageDef.LOCKED.UNLOCKED != merchantUserValidate.getIsLocked()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_USER_ENABLE, localeMessageSourceService.getMessage("common.user.islocked"));
			}

			// 密码输入错误次数校验
			String username = MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantUserValidate.getIdNumber();
			if (RedisUtil.islimit_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日登录密码错误次数限制");
			}

			if (!encoder.matches(userRsetPassword.getPassword(), merchantUserValidate.getPassword())) {
				int count = RedisUtil.increment_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
				return new ResponseRestEntity<MerchantUser>(CommonFun.errorPassword(count), "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}
		// 修改密码
		merchantUserValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		merchantUserValidate.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);
		merchantUserService.updateByPrimaryKeySelective(merchantUserValidate);
		merchantUserValidate.setPassword("");
		merchantUserValidate.setPayPassword("");
		merchantUserValidate.setEmail(CommonFun.getEmailWhithStar(merchantUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, merchantUserValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(merchantUserValidate, HttpRestStatus.OK);
	}

	// 重置支付密码
	@PreAuthorize("hasRole('R_SELLER_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Reset users paypassword", notes = "")
	@RequestMapping(value = "/reset/paypassword", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> resetConsumerPayPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_SAME, "新支付密码和旧支付密码一致，请重新输入");
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_NOT_SAME, "确认支付密码和新支付密码不一致，请重新输入!");
		}

		// 校验旧密码的正确性
		MerchantUser merchantUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			merchantUserValidate = merchantUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == merchantUserValidate) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_NOT_NULL, "没有相应的用户!");
		} else {

			if (MessageDef.STATUS.ENABLE_INT != merchantUserValidate.getStatus()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_OLD_ERROR, "用户暂时没有被启用，请启用后再设置密码!");
			}
			if (MessageDef.LOCKED.UNLOCKED != merchantUserValidate.getIsLocked()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_USER_ENABLE, "用户已经被锁定，请解锁后再重置密码!");
			}

			// 支付密码输入错误次数校验
			if (RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(merchantUserValidate.getUserId(), Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日支付密码错误次数限制");
			}

			if (!encoder.matches(userRsetPassword.getPassword(), merchantUserValidate.getPayPassword())) {
				int count = RedisUtil.increment_ERROR_PAY_PASSWORD_COUNT(merchantUserValidate.getUserId(), Calendar.getInstance().getTime());
				return new ResponseRestEntity<MerchantUser>(CommonFun.errorPayPassword(count), "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PAY_PASSWORD_COUNT(merchantUserValidate.getUserId(), Calendar.getInstance().getTime());
		}

		boolean isPass = CommonFun.checkPayPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PASSWORD_PAY_FORMAT_ERROR, "pay password must 6 length.and must number");
		}

		// 修改支付密码
		merchantUserValidate.setPayPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		merchantUserValidate.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.NOT_DEFUALT);
		merchantUserService.updateByPrimaryKeySelective(merchantUserValidate);
		merchantUserValidate.setPassword("");
		merchantUserValidate.setPayPassword("");
		merchantUserValidate.setEmail(CommonFun.getEmailWhithStar(merchantUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPAYPWD, merchantUserValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(merchantUserValidate, HttpRestStatus.OK);

	}

	// 重置密码
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "Reset users password to default", notes = "")
	@RequestMapping(value = "/reset/defaultPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> resetDefaultPassword(@PathVariable("id") String id, @Valid @RequestBody MerchantUser merchantUser) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		MerchantUser merchantUserValidate = merchantUserService.selectByPrimaryKey(id);
		if (null == merchantUserValidate) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		// 修改支付密码
		merchantUserValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		merchantUserValidate.setPassword(encoder.encode(merchantUser.getPassword()));
		merchantUserValidate.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		merchantUserService.updateByPrimaryKeySelective(merchantUserValidate);
		merchantUserValidate.setPassword(null);
		merchantUserValidate.setPayPassword(null);
		merchantUserValidate.setEmail(CommonFun.getEmailWhithStar(merchantUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, merchantUserValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(merchantUserValidate, HttpRestStatus.OK);
	}

	// 重置支付密码
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "Reset users pay assword to default", notes = "")
	@RequestMapping(value = "/reset/defaultPayPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> resetDefaultPayPassword(@PathVariable("id") String id, @Valid @RequestBody MerchantUser merchantUser) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		MerchantUser merchantUserValidate = merchantUserService.selectByPrimaryKey(id);
		if (null == merchantUserValidate) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		// 修改支付密码
		merchantUserValidate.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.DEFUALT);
		merchantUserValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		merchantUserValidate.setPayPassword(encoder.encode(merchantUser.getPayPassword()));
		merchantUserService.updateByPrimaryKeySelective(merchantUserValidate);
		merchantUserValidate.setPassword(null);
		merchantUserValidate.setPayPassword(null);
		merchantUserValidate.setEmail(CommonFun.getEmailWhithStar(merchantUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPAYPWD, merchantUserValidate, CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUser>(merchantUserValidate, HttpRestStatus.OK);
	}

	// 选择框seller数据
	@PreAuthorize("hasRole('R_SELLER_Q')")
	@ApiOperation(value = "Query the clapStorNo, support paging", notes = "")
	@RequestMapping(value = "/seller", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantUser>> selectClapStoreNo(@RequestParam(required = false) String id, @RequestParam(required = false) String clapStoreNo, @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		MerchantUser merchantUser = new MerchantUser();

		merchantUser.setUserId(id);
		merchantUser.setIdNumber(idNumber);
		merchantUser.setClapStoreNo(clapStoreNo);

		int count = merchantUserService.count(merchantUser);

		if (count == 0) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantUser> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantUserService.selectPage(merchantUser);
		} else {
			list = merchantUserService.selectPage(merchantUser);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantUser>>(new ArrayList<MerchantUser>(), HttpRestStatus.NOT_FOUND);
		}
		for (MerchantUser merchantUser2 : list) {
			merchantUser2.setPassword("");
			merchantUser2.setPayPassword("");

			Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, merchantUser2.getUserId(), merchantUser2.getBalance());
			merchantUser2.setBalance(balance);

			merchantUser2.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantUser2.getIdNumber(), Calendar.getInstance().getTime()));
			merchantUser2.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(merchantUser2.getUserId(), Calendar.getInstance().getTime()));

			merchantUser2.setEmail(CommonFun.getEmailWhithStar(merchantUser2.getEmail()));
		}

		return new ResponseRestEntity<List<MerchantUser>>(list, HttpRestStatus.OK, count, count);
	}

	///////////////////////////////////////////////// uplaod///User//Img//Start//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@PreAuthorize("hasRole('R_SELLER_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "upload user picture", notes = "")
	@PostMapping("/upload/picture/{id}") // //new annotation since 4.3
	public ResponseRestEntity<Picture> singleFileUpload(@RequestParam("avatar_src") Object src, @RequestParam("avatar_data") String data, @RequestParam("avatar_file") MultipartFile file,
			RedirectAttributes redirectAttributes, @PathVariable("id") String id) {

		HttpHeaders headers = new HttpHeaders();
		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
			return new ResponseRestEntity<Picture>(headers, HttpRestStatus.BAD_REQUEST, localeMessageSourceService.getMessage("common.upload.file"));
		}

		// ie file.getOriginalFilename全路径 chrome为文件名
		String tmpFileName = "";
		File fileTmp = new File(file.getOriginalFilename());
		tmpFileName = fileTmp.getName();

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
			Path path = Paths.get(TMP_FOLDER + tmpFileName);
			Files.write(path, bytes);
			String userImgUrl = IMG_PRE + id + "_" + System.currentTimeMillis();
			String destPath = REL_FOLDER + MessageDef.UPLOAD_PATH.MERCHANT;
			File destPathDir = new File(destPath);
			if (!destPathDir.exists() && !destPathDir.isDirectory()) {
				destPathDir.mkdirs();
			}

			destPath += "/" + userImgUrl;
			File srcFile = new File(TMP_FOLDER + tmpFileName);
			if (!(getPicFormate(srcFile).equalsIgnoreCase(MessageDef.IMAGETYPE.JPG) || getPicFormate(srcFile).equalsIgnoreCase(MessageDef.IMAGETYPE.JPEG))) {
				return new ResponseRestEntity<Picture>(headers, HttpRestStatus.IMAGE_UPLOAD_TYPE_ERROR, localeMessageSourceService.getMessage("common.img.upload.typeFailed"));
			}

			if (datas != null && datas.length == 5) {
				x = Double.valueOf(datas[0].substring(datas[0].indexOf(":") + 1)).intValue();
				y = Double.valueOf(datas[1].substring(datas[1].indexOf(":") + 1)).intValue();
				w = Double.valueOf(datas[2].substring(datas[2].indexOf(":") + 1)).intValue();
				h = Double.valueOf(datas[3].substring(datas[3].indexOf(":") + 1)).intValue();
				cutImage(srcFile, destPath, x, y, w, h);
			} else {
				Path path2 = Paths.get(destPath);
				Files.write(path2, bytes);
			}

			srcFile.delete();
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + tmpFileName + "'");

			MerchantUser existMerchantUser = merchantUserService.selectByPrimaryKey(id);
			deleteOldImgFile(existMerchantUser);// 删除旧的头像图片
			MerchantUser merchantUser = new MerchantUser();
			merchantUser.setUserId(id);
			merchantUser.setUserImgUrl(userImgUrl);
			merchantUserService.updateByPrimaryKeySelective(merchantUser);
			// 修改日志
			merchantUser.setPassword("");
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, merchantUser, CommonLogImpl.MERCHANT);
			Picture pic = new Picture();
			pic.setPicName(userImgUrl);

			return new ResponseRestEntity<Picture>(pic, headers, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.upload.success"));

		} catch (Exception e) {
			log.error("UploadTestController.singleFileUpload exception", e);
			return new ResponseRestEntity<Picture>(headers, HttpRestStatus.IMAGE_UPLOAD_FAILED_ERROR, localeMessageSourceService.getMessage("common.img.unload.fail"));
		}
		// return new ResponseRestEntity<Picture>(headers, HttpRestStatus.UNKNOWN,localeMessageSourceService.getMessage("common.upload.fail"));
	}

	private boolean deleteOldImgFile(MerchantUser existMerchantUser) {
		if (existMerchantUser != null && existMerchantUser.getUserImgUrl() != null) {
			File userImgFile = new File(existMerchantUser.getUserImgUrl());
			if (userImgFile.exists() && userImgFile.isFile()) {
				if (userImgFile.delete()) {
					return true;
				} else {
					log.error("userImgFile: " + existMerchantUser.getUserImgUrl() + " delete failed with unknow error.");
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

	/**
	 * 检查图片格式
	 * 
	 * @param file
	 * @return
	 */
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
			File readPath = new File(READ_FOLDER + MessageDef.UPLOAD_PATH.MERCHANT);
			if (!readPath.exists() && !readPath.isDirectory()) {
				readPath.mkdir();
			}

			InputStreamResource resource = new InputStreamResource(new FileInputStream(READ_FOLDER + MessageDef.UPLOAD_PATH.MERCHANT + "/" + path));
			return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("image/jpeg")).body(resource);
		} catch (Exception e) {
			log.error("", e);
		}

		return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
	}

	///////////////////////////////////////////////// get///User//Img//End//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 查询余额
	@PreAuthorize("hasRole('R_SELLER_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Check balances", notes = "")
	@RequestMapping(value = "/queryBalance", method = RequestMethod.GET)
	public ResponseRestEntity<QueryMerchantBalanceVo> queryMerchantBalance(@RequestParam("userName") String userName) {
		if (null == userName || "".equals(userName)) {
			return new ResponseRestEntity<QueryMerchantBalanceVo>(HttpRestStatus.NOTEMPTY, localeMessageSourceService.getMessage("common.username.empty"));
		}

		Double merchantBalance = merchantUserService.queryMerchantBalance(userName);

		QueryMerchantBalanceVo queryBalance = new QueryMerchantBalanceVo();
		queryBalance.setUserName(userName);

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, userName);
		queryBalance.setBalance(balance);

		return new ResponseRestEntity<QueryMerchantBalanceVo>(queryBalance, HttpRestStatus.OK);

	}

	@PreAuthorize("hasRole('R_SELLER_E')")
	@RequestMapping(value = "/singleUpload", method = RequestMethod.POST)
	public Map<String, Object> singleFileUpload(HttpServletRequest request, @RequestParam("name") String name) throws Exception {

		String fileName = "UPLOAD_SELLER_ERROR.txt";

		File readAllPath = new File(UPLOAD_FILE_FOLDER);
		if (readAllPath.exists()) {
			File[] files = readAllPath.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File tmp = files[i];
					if (tmp.toString().equals(UPLOAD_FILE_FOLDER + fileName)) {
						tmp.delete();
					}
				}
			}

		}

		String filePre = System.currentTimeMillis() + "";
		name = filePre + "_" + name;

		// System.out.println("after name:"+name);
		// System.out.println("consumerGroupId:"+consumerGroupId);

		String path = UPLOAD_FILE_FOLDER;// request.getSession().getServletContext().getRealPath("upload");
		// System.out.println("path:"+path);

		File targetFile = new File(path, name);
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}

		write(path, name, request.getInputStream());

		int import_int = insertToDB(targetFile);

		Map<String, Object> result_map = new HashMap<String, Object>();
		result_map.put("importNum", import_int);
		return result_map;
	}

	private void write(String path, String filename, InputStream in) {
		// System.out.println("写入文件");

		File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) {// 若创建文件夹不成功
				// System.out.println("Unable to create external cache directory");
			}
		}

		File targetfile = new File(path + filename);
		OutputStream os = null;
		try {
			os = new FileOutputStream(targetfile);
			int ch = 0;
			while ((ch = in.read()) != -1) {
				os.write(ch);
			}
			os.flush();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			try {
				os.close();
				in.close();
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private int insertToDB(File file) {
		// System.out.println("准备写入数据库中...字符集");
		int import_int = 0;
		try {
			// BufferedReader in =new BufferedReader(new FileReader(file));
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader in = new BufferedReader(isr);
			// List<String> list = new ArrayList<String>();
			String s;
			int line_num = 0;
			while ((s = in.readLine()) != null) {

				boolean successFlag = false;

				line_num = line_num + 1;
				String[] str = s.split(",");
				if (str != null) {
					String idNumber = null;
					String name = null;
					String clapCode = null;
					if (str.length == 3) {
						idNumber = str[0];
						name = str[1];
						clapCode = str[2];

						if (!CommonFun.isEmpty(idNumber)) {
							idNumber = idNumber.trim();

							// 为了解决txt中的第一行的问题 start
							// 例如第一行数据是1按照常规来看长度应该是1但是长度显示是2 故需要去除第一位得到后面的数据才是真实的数据
							if (line_num == 1 && idNumber.length() >= 2) {
								idNumber = ImportUtil.getValueForUTF_8(idNumber);
							}
							// 为了解决txt中的第一行的问题 end

							String userId = MessageDef.USER_TYPE.MERCHANT_STRING + idNumber;

							MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(userId);
							if (merchantUser == null) {// 没有则新增
								try {// 如果抛异常，则放弃本次操作。循环下一个。
										// 先插入消费用户表
									MerchantUser merchantUserTmp = new MerchantUser();
									merchantUserTmp.setUserId(userId);
									merchantUserTmp.setUserName(name);
									// 后台添加字段 start
									merchantUserTmp.setIdNumber(idNumber);// bug 修改
									merchantUserTmp.setBalance((double) 0);// 余额为0
									merchantUserTmp.setStatus(0);// 状态为启用
									// 密码
									BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
									// merchantUser.setPassword(encoder.encode(DEFAULT_PASSWORD));
									// merchantUser.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD));
									if (merchantUserTmp.getIdNumber() != null) {
										/* String number= merchantUserTmp.getIdNumber().replaceAll("[^0-9]+",""); */

										String pwd = CommonFun.generaPassword(idNumber, 6);
										merchantUserTmp.setPassword(encoder.encode(pwd));// 加密消耗时间88ms
										// 支付密码
										String number = idNumber.replaceAll("[^0-9]+", "");
										String paypwd = CommonFun.generaPassword(number, 6);
										merchantUserTmp.setPayPassword(encoder.encode(paypwd));
									} else {
										merchantUserTmp.setPassword(encoder.encode(DEFAULT_PASSWORD));
										merchantUserTmp.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD));
									}
									int limitNumber = MessageDef.EMPLOYEE_LIMIT.LIMIT;
									merchantUserTmp.setEmployeeLimit(limitNumber);
									merchantUserTmp.setIsLocked(0);// 未冻结

									merchantUserTmp.setIsBindBankCard(0);// 未绑定
									merchantUserTmp.setIsFirstLogin(1);// 首次登陆
									merchantUserTmp.setCreateTime(PageHelperUtil.getCurrentDate());
									// 后台添加字段 end
									merchantUserTmp.setIdNumber(CommonFun.getRelVid(idNumber));
									merchantUserTmp.setClapStoreNo(clapCode);
									merchantUserTmp.setIsActive(0);
									merchantUserTmp.setIsDefaultPassword(1);
									merchantUserTmp.setIsDefaultPayPassword(1);
									merchantUserTmp.setEmailBindStatus(0);
									// 再插入消费用户clap卡
									MerchantRoleUserKey merchantRoleUserKeyTmp = new MerchantRoleUserKey();
									merchantRoleUserKeyTmp.setUserId(userId);
									merchantRoleUserKeyTmp.setRoleId("1");
									merchantRoleUserService.insert(merchantRoleUserKeyTmp);
									merchantUserService.insert(merchantUserTmp);
									// 新增日志
									merchantUserTmp.setPassword("");
									merchantUserTmp.setPayPassword("");
									CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, merchantUserTmp, CommonLogImpl.MERCHANT);

									import_int = import_int + 1;
									successFlag = true;
								} catch (Exception e) {
									log.error("load file error", e);
								}

							}
						}

					}

				}

				// 上传失败错误记录
				if (!successFlag) {
					// System.out.println(s);
					try {
						String name = "UPLOAD_SELLER_ERROR.txt";
						String path = UPLOAD_FILE_FOLDER;
						File targetFile = new File(path, name);

						FileWriter fw = new FileWriter(targetFile, true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(s + "\r\n");// 往已有的文件上添加字符串
						bw.close();
						fw.close();
					} catch (Exception e) {
						log.error("", e);
					}
				}

			}
			return import_int;
		} catch (Exception e) {
			log.error("", e);
		}

		return 0;
	}

	@PreAuthorize("hasRole('R_SELLER_Q')")
	@ApiOperation(value = "download errorLog", notes = "")
	@RequestMapping(value = "/get/errorLog", method = RequestMethod.GET)
	public void hello(HttpServletResponse res) throws IOException {

		String fileName = "UPLOAD_SELLER_ERROR.txt";
		File readPath = new File(UPLOAD_FILE_FOLDER);
		if (!readPath.exists() && !readPath.isDirectory()) {
			readPath.mkdir();
		}

		File readAllPath = new File(UPLOAD_FILE_FOLDER + fileName);
		if (!readAllPath.exists()) {
			File file = new File(readPath, fileName);
			file.createNewFile();
		}

		res.setHeader("content-type", "application/octet-stream");
		res.setContentType("application/octet-stream");
		res.setHeader("Content-Disposition", "attachment;filename=" + "UPLOAD_SELLER_ERROR.txt");
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = res.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(new File(UPLOAD_FILE_FOLDER + fileName)));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
		// System.out.println("success");
	}

	// 校验支付密码
	@PreAuthorize("hasRole('R_SELLER_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/validate/paypassword", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUser> validatePaypassword(@RequestParam("id") String id, @RequestParam("password") String password) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// 校验旧密码的正确性
		MerchantUser merchantUserValidate = null;
		if (id != null && !id.isEmpty()) {
			merchantUserValidate = merchantUserService.selectByPrimaryKey(id);
		}
		if (null == merchantUserValidate) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (MessageDef.STATUS.ENABLE_INT != merchantUserValidate.getStatus()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}

			if (MessageDef.LOCKED.UNLOCKED != merchantUserValidate.getIsLocked()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("common.user.islocked"));
			}

			// 支付密码输入错误次数校验
			if (RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(id, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日支付密码错误次数限制");
			}

			if (!encoder.matches(password, merchantUserValidate.getPayPassword())) {
				int count = RedisUtil.increment_ERROR_PAY_PASSWORD_COUNT(id, Calendar.getInstance().getTime());
				return new ResponseRestEntity<MerchantUser>(CommonFun.errorPayPassword(count), "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PAY_PASSWORD_COUNT(id, Calendar.getInstance().getTime());
		}

		merchantUserValidate.setPassword("");
		merchantUserValidate.setPayPassword("");
		merchantUserValidate.setEmail(CommonFun.getEmailWhithStar(merchantUserValidate.getEmail()));
		return new ResponseRestEntity<MerchantUser>(merchantUserValidate, HttpRestStatus.OK);
	}

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

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(userId);
		if (merchantUser == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		}

		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_BIND_SUBJECT);
		String vCode = CommonFun.getVcode();
		String content = CommonFun.getBindEmailContent(merchantUser.getUserName(), vCode, email);
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
		MerchantUser merchantUser = new MerchantUser();
		merchantUser.setUserId(userId);
		merchantUser.setEmail(email);
		merchantUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.BIND);
		merchantUserService.updateByPrimaryKeySelective(merchantUser);

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

		MerchantUser merchantUser = new MerchantUser();
		merchantUser.setUserId(userId);
		merchantUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.NOT_REMIND);
		merchantUserService.updateByPrimaryKeySelective(merchantUser);

		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 通过vid查询用户信息
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/vid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantUser> selectByVid(@RequestParam("vid") String vid) {
		vid = CommonFun.getRelVid(vid);
		MerchantUser merchantUser = merchantUserService.selectByIdNumber(vid);
		if (merchantUser == null) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.NOT_FOUND);
		}
		merchantUser.setPassword(null);
		merchantUser.setPayPassword(null);

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(merchantUserService, merchantUser.getUserId(), merchantUser.getBalance());
		merchantUser.setBalance(balance);

		merchantUser.setEmail(CommonFun.getEmailWhithStar(merchantUser.getEmail()));
		return new ResponseRestEntity<MerchantUser>(merchantUser, HttpRestStatus.OK);
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

		MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(userId);
		if (merchantUser == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		}
		if (!email.equals(merchantUser.getEmail())) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_NOT_MATH_ERROR, "email not math error");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		MerchantUser merchantUserTmp = new MerchantUser();
		merchantUserTmp.setUserId(merchantUser.getUserId());

		String loginPassword = null;
		String payPassword = null;
		if (passwordType.contains(MessageDef.PASSWORD_TYPE.LOGIN_PASSORD)) {
			loginPassword = CommonFun.getLoginPassword();
			merchantUserTmp.setPassword(encoder.encode(loginPassword));
			merchantUserTmp.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		}

		if (passwordType.contains(MessageDef.PASSWORD_TYPE.PAY_PASSWORD)) {
			payPassword = CommonFun.getPayPassword();
			merchantUserTmp.setPayPassword(encoder.encode(payPassword));
			merchantUserTmp.setIsDefaultPayPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		}
		merchantUserService.updateByPrimaryKeySelective(merchantUserTmp);
		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_RETRIEVE_PASSWORD_SUBJECT);
		String content = CommonFun.getRetrievePassordContent(merchantUser.getUserName(), email, loginPassword, payPassword);
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
	public ResponseRestEntity<MerchantUser> validateLoginpassword(@RequestParam("id") String id, @RequestParam("password") String password) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		MerchantUser merchantUserValidate = null;
		if (id != null && !id.isEmpty()) {
			merchantUserValidate = merchantUserService.selectByPrimaryKey(id);
		}
		if (null == merchantUserValidate) {
			return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (MessageDef.STATUS.ENABLE_INT != merchantUserValidate.getStatus()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}

			if (MessageDef.LOCKED.UNLOCKED != merchantUserValidate.getIsLocked()) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("common.user.islocked"));
			}

			// 密码输入错误次数校验
			String username = MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantUserValidate.getIdNumber();
			if (RedisUtil.islimit_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<MerchantUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日登录密码错误次数限制");
			}

			if (!encoder.matches(password, merchantUserValidate.getPassword())) {
				int count = RedisUtil.increment_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
				MerchantUser merchantUser = new MerchantUser();
				merchantUser.setRemainingTimes(count);
				return new ResponseRestEntity<MerchantUser>(merchantUser, HttpRestStatus.PASSWORD_ERROR, "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
		}

		merchantUserValidate.setPassword("");
		merchantUserValidate.setPayPassword("");
		merchantUserValidate.setEmail(CommonFun.getEmailWhithStar(merchantUserValidate.getEmail()));
		return new ResponseRestEntity<MerchantUser>(merchantUserValidate, HttpRestStatus.OK);
	}

	// 解锁登录密码
	@PreAuthorize("hasRole('R_SELLER_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockLoginPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockLoginPassowrd(@RequestParam(required = false) String userId, @RequestParam(required = false) String idNumber) {
		idNumber = CommonFun.getRelVid(idNumber);
		if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(idNumber)) {

			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}

		if (StringUtils.isNotEmpty(userId)) {
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(userId);
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + merchantUser.getIdNumber(), Calendar.getInstance().getTime());
		}

		if (StringUtils.isNotEmpty(idNumber)) {
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.MERCHANT_STRING + RedisDef.DELIMITER.UNDERLINE + idNumber, Calendar.getInstance().getTime());
		}
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 解锁支付密码
	@PreAuthorize("hasRole('R_SELLER_E')")
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