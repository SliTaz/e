package com.zbensoft.e.payment.api.control;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import java.util.Random;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
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

import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONObject;
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
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.common.gif.Captcha;
import com.zbensoft.e.payment.api.common.gif.GifCaptcha;
import com.zbensoft.e.payment.api.exception.RegisterApiFailException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseBodyNotExistException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseFormatErrorException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseIsNullException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseStatesNotSuccException;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIGetpatriotResponse;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIRequest;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterImgValidateResponse;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterRequest;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterResponse;
import com.zbensoft.e.payment.api.vo.consumer.QueryConsumerBalanceVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.ImportUtil;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.ConsumerUserQRCode;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.UserRsetPassword;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerUser")
@RestController
public class ConsumerUserController {

	private static final Logger log = LoggerFactory.getLogger(ConsumerUserController.class);

	@Autowired
	ConsumerUserService consumerUserService;

	@Autowired
	ConsumerUserClapService consumerUserClapService;

	@Autowired
	MerchantUserService merchantUserService;
	@Autowired
	ConsumerFamilyService consumerFamilyService;

	@Autowired
	ConsumerRoleUserService consumerRoleUserService;

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

	@Value("${url.register.get_patriot}")
	private String URL_REGISTER_GET_PATRIOT;
	@Value("${url.register.register_update_wallet}")
	private String URL_REGISTER_REGISTER_UPDATE_WALLET;

	private CloseableHttpClient httpClient = SpringBeanUtil.getBean(CloseableHttpClient.class);
	private RequestConfig config = SpringBeanUtil.getBean(RequestConfig.class);

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_BUYER_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query the consumerUser, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUser>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String idNumber, @RequestParam(required = false) String userName,
			@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) Integer status, @RequestParam(required = false) Integer isLocked, @RequestParam(required = false) Integer isBindClap,
			@RequestParam(required = false) Integer isBindBankCard, @RequestParam(required = false) String password, @RequestParam(required = false) Integer isFirstLogin, @RequestParam(required = false) String remark,
			@RequestParam(required = false) String email, @RequestParam(required = false) Integer emailBindStatus, @RequestParam(required = false) Integer isActive,
			@RequestParam(required = false) Integer isDefaultPassword, @RequestParam(required = false) Integer isDefaultPayPassword, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		// consumerUserService.count();
		ConsumerUser consumerUser = new ConsumerUser();

		// 必须输入一个进行查询
		if ((idNumber == null || "".equals(idNumber)) && (id == null || "".equals(id)) && (phoneNumber == null || "".equals(phoneNumber))) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}

		if (idNumber == null || "".equals(idNumber)) {
			consumerUser.setUserId(id);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (id == null || "".equals(id)) {
					consumerUser.setUserId(consumerUserClap.getUserId());
				} else {
					if (id.equals(consumerUserClap.getUserId())) {
						consumerUser.setUserId(id);
					} else {
						return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}

		consumerUser.setPhoneNumber(phoneNumber);
		consumerUser.setUserName(userName);
		consumerUser.setStatus(status);
		consumerUser.setIsLocked(isLocked);
		consumerUser.setIsBindClap(isBindClap);
		consumerUser.setIsBindBankCard(isBindBankCard);
		consumerUser.setPassword(password);
		consumerUser.setIsFirstLogin(isFirstLogin);
		consumerUser.setRemark(remark);
		consumerUser.setEmail(email);
		consumerUser.setEmailBindStatus(emailBindStatus);
		consumerUser.setIsActive(isActive);
		consumerUser.setIsDefaultPassword(isDefaultPassword);
		consumerUser.setIsDefaultPayPassword(isDefaultPayPassword);

		int count = consumerUserService.count(consumerUser);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerUser> list = null;// consumerUserService.selectPage(consumerUser);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerUserService.selectPage(consumerUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerUserService.selectPage(consumerUser);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerUser> listNew = new ArrayList<ConsumerUser>();
		for (ConsumerUser bean : list) {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(bean.getUserId());
			if (consumerUserClap != null) {
				bean.setIdNumber(consumerUserClap.getIdNumber());
			}
			Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(consumerUserService, consumerUser.getUserId(), bean.getBalance());
			bean.setBalance(balance);

			bean.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + consumerUserClap.getIdNumber(), Calendar.getInstance().getTime()));
			bean.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(bean.getUserId(), Calendar.getInstance().getTime()));

			bean.setPassword("");
			bean.setPayPassword("");
			bean.setEmail(CommonFun.getEmailWhithStar(bean.getEmail()));

			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerUser>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_BUYER_Q')")
	@ApiOperation(value = "Query the consumerUser, support paging", notes = "")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUser>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String userName, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		ConsumerUser consumerUser = new ConsumerUser();
		consumerUser.setUserId(id);
		consumerUser.setUserName(userName);

		int count = consumerUserService.count(consumerUser);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerUser> list = null;// consumerUserService.selectPage(consumerUser);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerUserService.selectPage(consumerUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerUserService.selectPage(consumerUser);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerUser> listNew = new ArrayList<ConsumerUser>();
		for (ConsumerUser bean : list) {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(bean.getUserId());
			if (consumerUserClap != null) {
				bean.setIdNumber(consumerUserClap.getIdNumber());
			}
			Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(consumerUserService, consumerUser.getUserId(), bean.getBalance());
			bean.setBalance(balance);

			bean.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + consumerUserClap.getIdNumber(), Calendar.getInstance().getTime()));
			bean.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(bean.getUserId(), Calendar.getInstance().getTime()));

			bean.setPassword("");
			bean.setPayPassword("");
			bean.setEmail(CommonFun.getEmailWhithStar(bean.getEmail()));

			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerUser>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询用户，支持分页
	@PreAuthorize("hasRole('R_BUYER_Q')")
	@ApiOperation(value = "Query the consumerUser, support paging", notes = "")
	@RequestMapping(value = "/bank", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUser>> selectPageAll(@RequestParam(required = false) String id, @RequestParam(required = false) String userName, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		ConsumerUser consumerUser = new ConsumerUser();
		consumerUser.setUserId(id);
		consumerUser.setUserName(userName);

		int count = consumerUserService.count(consumerUser);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerUser> list = null;// consumerUserService.selectPage(consumerUser);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerUserService.selectPage(consumerUser);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerUserService.selectPage(consumerUser);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		}

		return new ResponseRestEntity<List<ConsumerUser>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户
	@PreAuthorize("hasRole('R_BUYER_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query the consumerUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerUser> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		consumerUser.setPassword(null);
		consumerUser.setPayPassword(null);

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(consumerUserService, consumerUser.getUserId(), consumerUser.getBalance());
		consumerUser.setBalance(balance);
		consumerUser.setEmail(CommonFun.getEmailWhithStar(consumerUser.getEmail()));

		return new ResponseRestEntity<ConsumerUser>(consumerUser, HttpRestStatus.OK);
	}

	// 新增用户
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "New consumerUsers", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerUser(@Valid @RequestBody ConsumerUser consumerUser, BindingResult result, UriComponentsBuilder ucBuilder) {

		consumerUser.setUserId(IDGenerate.generateCONSUMER_USER_ID());
		consumerUser.setCreateTime(PageHelperUtil.getCurrentDate());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		if (consumerUserService.isConsumerUserExist(consumerUser)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT, localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		consumerUser.setPassword(encoder.encode(consumerUser.getPassword()));
		consumerUser.setPayPassword(encoder.encode(consumerUser.getPayPassword()));
		// 支付密码，默认密码规则
		/*
		 * List<ConsumerUserClap> list = consumerUserClapService.selectByUserId(consumerUser.getUserId()); if (list != null && list.size() > 0) { String idNumber =
		 * list.get(0).getIdNumber(); if (idNumber != null) { String number= idNumber.replaceAll("[^0-9]+","");
		 * 
		 * String pwd = PageHelperUtil.generaPassword(number, 6); consumerUser.setPassword(encoder.encode(pwd)); consumerUser.setPayPassword(encoder.encode(pwd)); } else {
		 * consumerUser.setPassword(encoder.encode(DEFAULT_PASSWORD)); consumerUser.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD)); } } else {
		 * consumerUser.setPassword(encoder.encode(DEFAULT_PASSWORD)); consumerUser.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD)); }
		 */
		consumerUserService.insert(consumerUser);
		// 新增日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerUser, CommonLogImpl.CONSUMER);
		// 关系表新增Start
		if (consumerUser.getRoleId() != null) {
			String[] idStr = consumerUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				ConsumerRoleUserKey roleUserKey = new ConsumerRoleUserKey();
				roleUserKey.setUserId(consumerUser.getUserId());
				roleUserKey.setRoleId(idStr[i]);
				consumerRoleUserService.insert(roleUserKey);
			}
		}
		// 关系表新增End
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerUser/{id}").buildAndExpand(consumerUser.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户信息
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "Modify consumerUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> updateConsumerUser(@PathVariable("id") String id, @Valid @RequestBody ConsumerUser consumerUser, BindingResult result) {

		ConsumerUser currentConsumerUser = consumerUserService.selectByPrimaryKey(id);
		List<ConsumerRoleUserKey> consumerUsersList = consumerRoleUserService.selectByUserId(id);
		if (currentConsumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerUser.setUserName(consumerUser.getUserName());
		currentConsumerUser.setPhoneNumber(consumerUser.getPhoneNumber());
		currentConsumerUser.setStatus(consumerUser.getStatus());

		currentConsumerUser.setIsLocked(consumerUser.getIsLocked());
		currentConsumerUser.setIsBindClap(consumerUser.getIsBindClap());
		currentConsumerUser.setIsBindBankCard(consumerUser.getIsBindBankCard());

		currentConsumerUser.setIsFirstLogin(consumerUser.getIsFirstLogin());
		currentConsumerUser.setCreateTime(consumerUser.getCreateTime());
		currentConsumerUser.setRemark(consumerUser.getRemark());
		currentConsumerUser.setEmail(consumerUser.getEmail());
		currentConsumerUser.setEmailBindStatus(consumerUser.getEmailBindStatus());
		currentConsumerUser.setIsActive(consumerUser.getIsActive());
		currentConsumerUser.setIsDefaultPassword(consumerUser.getIsDefaultPassword());
		currentConsumerUser.setIsDefaultPayPassword(consumerUser.getIsDefaultPayPassword());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				// System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerUser>(currentConsumerUser, HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerUserService.updateByPrimaryKey(currentConsumerUser);
		// 修改日志
		currentConsumerUser.setPassword("");
		currentConsumerUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerUser, CommonLogImpl.CONSUMER);
		// 关系表修改start(逻辑:先删除，然后增加)
		if (consumerUsersList != null && consumerUsersList.size() > 0) {
			for (ConsumerRoleUserKey roleUserKey : consumerUsersList) {
				consumerRoleUserService.deleteByPrimaryKey(roleUserKey);
			}
		}

		if (consumerUser.getRoleId() != null && !"".equals(consumerUser.getRoleId())) {
			String[] idStr = consumerUser.getRoleId().split(",");
			for (int i = 0; i < idStr.length; i++) {
				ConsumerRoleUserKey roleUserKey = new ConsumerRoleUserKey();
				roleUserKey.setUserId(consumerUser.getUserId());
				roleUserKey.setRoleId(idStr[i]);
				consumerRoleUserService.insert(roleUserKey);
			}
		}
		// 关系表修改start(逻辑:先删除，然后增加)
		return new ResponseRestEntity<ConsumerUser>(currentConsumerUser, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户信息
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "Modify part of the consumerUser information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerUser> updateConsumerUserSelective(@PathVariable("id") String id, @RequestBody ConsumerUser consumerUser) {

		ConsumerUser currentConsumerUser = consumerUserService.selectByPrimaryKey(id);

		if (currentConsumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		consumerUser.setUserId(id);
		consumerUserService.updateByPrimaryKeySelective(consumerUser);
		// 修改日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerUser, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(currentConsumerUser, HttpRestStatus.OK);
	}

	// 删除指定用户
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "Delete the specified consumerUser", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerUser> deleteConsumerUser(@PathVariable("id") String id) {

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		List<ConsumerRoleUserKey> list = consumerRoleUserService.selectByUserId(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}

		consumerUserService.deleteByPrimaryKey(id);
		// 删除日志开始
		ConsumerUser consumer = new ConsumerUser();
		consumer.setUserId(id);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer, CommonLogImpl.CONSUMER);
		// 删除日志结束
		// 关系表删除start
		if (list != null && list.size() > 0) {
			for (ConsumerRoleUserKey consumerRoleUserKey : list) {
				consumerRoleUserService.deleteByPrimaryKey(consumerRoleUserKey);
			}
		}
		// 关系表删除end
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NO_CONTENT);
	}

	// 批量
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "update Many merchantUser", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> deleteSysLogMany(@PathVariable("id") String id) {
		String[] idStr = id.split(",");
		if (idStr != null) {
			for (String str : idStr) {
				ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(str);
				if (consumerUser == null) {
					return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
				}
				// 改变用户状态 0:启用 1:停用
				consumerUser.setIsActive(1);
				consumerUserService.updateByPrimaryKey(consumerUser);

				/*
				 * //修改日志 merchantUser.setPassword(""); merchantUser.setPayPassword(""); CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE,
				 * merchantUser,CommonLogImpl.MERCHANT);
				 */
			}
		}
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 用户启用
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "enable the specified consumerUser", notes = "")
	@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> enableConsumerUser(@PathVariable("id") String id) {

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		consumerUser.setStatus(0);
		consumerUserService.updateByPrimaryKey(consumerUser);
		// 修改日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		consumerUser.setEmail(CommonFun.getEmailWhithStar(consumerUser.getEmail()));
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, consumerUser, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 用户停用
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "enable the specified consumerUser", notes = "")
	@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> disableConsumerUser(@PathVariable("id") String id) {

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		// 改变用户状态 0:启用 1:停用
		consumerUser.setStatus(1);
		consumerUserService.updateByPrimaryKey(consumerUser);
		// 修改日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		consumerUser.setEmail(CommonFun.getEmailWhithStar(consumerUser.getEmail()));
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, consumerUser, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 用户解冻
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "unfrost the specified consumerUser", notes = "")
	@RequestMapping(value = "/unfrost/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> unfrostConsumerUser(@PathVariable("id") String id) {

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}

		consumerUser.setIsLocked(0);
		consumerUserService.updateByPrimaryKey(consumerUser);
		// 修改日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		consumerUser.setEmail(CommonFun.getEmailWhithStar(consumerUser.getEmail()));
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UNFROST, consumerUser, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 用户冻结
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "frost the specified consumerUser", notes = "")
	@RequestMapping(value = "/frost/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> frostConsumerUser(@PathVariable("id") String id) {

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}

		consumerUser.setIsLocked(1);
		consumerUserService.updateByPrimaryKey(consumerUser);
		// 修改日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		consumerUser.setEmail(CommonFun.getEmailWhithStar(consumerUser.getEmail()));
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_FROST, consumerUser, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 用户还原
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "restore consumerUser by default", notes = "")
	@RequestMapping(value = "/restore/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> restoreConsumerUser(@PathVariable("id") String id) {

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(id);
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(id);
		if (consumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (consumerUserClap.getClapNo() != null) {
			String number = consumerUserClap.getClapNo().replaceAll("[^0-9]+", "");
			// 登陆密码
			String pwd = encoder.encode(CommonFun.generaPassword(number, 6));
			consumerUser.setPassword(pwd);
			// 支付密码
			consumerUser.setPayPassword(pwd);
		} else {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PATRIMONY_CARD_NOT_EXIST);
		}
		consumerUser.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		consumerUser.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		consumerUser.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.DEFUALT);
		consumerUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.UN_BIND);
		consumerUser.setEmail(null);
		consumerUserService.updateByPrimaryKey(consumerUser);
		// 修改日志
		consumerUser.setPassword("");
		consumerUser.setPayPassword("");
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESTORE, consumerUser, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 时间激活
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "activate the consumerUser by time", notes = "")
	@RequestMapping(value = "/activateConsumerBytime", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> activateConsumerBytime(@RequestBody ConsumerUser consumerUser, BindingResult result) {
		consumerUser.setIsActive(MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE);

		// 校验空
		if ((consumerUser.getTimeStart() == null || "".equals(consumerUser.getTimeStart())) && (consumerUser.getTimeEnd() == null || "".equals(consumerUser.getTimeEnd()))) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.CONSUMER_ACTIVATE_TIME_NOTEMPTY);
		}

		// 校验开始时间大于结束时间
		if ((consumerUser.getTimeStart() != null && !"".equals(consumerUser.getTimeStart())) && (consumerUser.getTimeEnd() != null && !"".equals(consumerUser.getTimeEnd()))) {

			try {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startDate = df.parse(consumerUser.getTimeStart());
				Date endDate = df.parse(consumerUser.getTimeEnd());
				if (startDate.getTime() > endDate.getTime()) {
					return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.STARTTIME_OVER_ENDTIME_ERROR);
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}

		consumerUserService.activateByTime(consumerUser);
		return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.OK);
	}

	// 重置登录密码
	@PreAuthorize("hasRole('R_BUYER_E') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Reset users password", notes = "")
	@RequestMapping(value = "/reset/password", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> resetConsumerPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_SAME, localeMessageSourceService.getMessage("common.password.same"));
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_NOT_SAME, localeMessageSourceService.getMessage("common.password.notsame"));
		}

		// 校验旧密码的正确性
		ConsumerUser consumerUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			consumerUserValidate = consumerUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_NOT_NULL, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (MessageDef.STATUS.ENABLE_INT != consumerUserValidate.getStatus()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_OLD_ERROR, localeMessageSourceService.getMessage("common.user.notenabled"));
			}
			if (MessageDef.LOCKED.UNLOCKED != consumerUserValidate.getIsLocked()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_USER_ENABLE, localeMessageSourceService.getMessage("common.user.islocked"));
			}

			List<ConsumerUserClap> consumerUserClapList = consumerUserClapService.selectByUserId(consumerUserValidate.getUserId());

			// 密码输入错误次数校验
			String username = MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + consumerUserClapList.get(0).getIdNumber();
			if (RedisUtil.islimit_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日登录密码错误次数限制");
			}

			// 校验密码
			if (!encoder.matches(userRsetPassword.getPassword(), consumerUserValidate.getPassword())) {

				int count = RedisUtil.increment_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
				return new ResponseRestEntity<ConsumerUser>(CommonFun.errorPassword(count), "密码错误，还可以尝试x次");
			}
			RedisUtil.delete_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
		}

		boolean isPass = CommonFun.checkLoginPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_LOGIN_FORMAT_ERROR, "login password must 6 length.and must contains number and a-z");
		}

		// 修改密码
		consumerUserValidate.setPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		consumerUserValidate.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);

		consumerUserService.updateByPrimaryKeySelective(consumerUserValidate);
		consumerUserValidate.setPassword("");
		consumerUserValidate.setPayPassword("");
		consumerUserValidate.setEmail(CommonFun.getEmailWhithStar(consumerUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, consumerUserValidate, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(consumerUserValidate, HttpRestStatus.OK);
	}

	// 重置支付密码
	@PreAuthorize("hasRole('R_BUYER_E') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Reset users paypassword", notes = "")
	@RequestMapping(value = "/reset/paypassword", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> resetConsumerPayPasswd(@RequestBody UserRsetPassword userRsetPassword, BindingResult result) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// 新密码和旧密码不能一样
		if (userRsetPassword.getNewPassword().equals(userRsetPassword.getPassword())) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_SAME, "新支付密码和旧支付密码一致，请重新输入");
		}

		// 确认密码要和新密码保持一致
		if (!userRsetPassword.getNewPassword().equals(userRsetPassword.getConfirmPassword())) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_NOT_SAME, "确认支付密码和新支付密码不一致，请重新输入!");
		}

		// 校验旧密码的正确性
		ConsumerUser consumerUserValidate = null;
		if (userRsetPassword.getUserId() != null && !userRsetPassword.getUserId().isEmpty()) {
			consumerUserValidate = consumerUserService.selectByPrimaryKey(userRsetPassword.getUserId());
		}
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_NOT_NULL, "没有相应的用户!");
		} else {

			if (MessageDef.STATUS.ENABLE_INT != consumerUserValidate.getStatus()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_OLD_ERROR, "用户暂时没有被启用，请启用后再设置密码!");
			}
			if (MessageDef.LOCKED.UNLOCKED != consumerUserValidate.getIsLocked()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_USER_ENABLE, "用户已经被锁定，请解锁后再重置密码!");
			}

			// 支付密码输入错误次数校验
			if (RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(consumerUserValidate.getUserId(), Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日支付密码错误次数限制");
			}

			if (!encoder.matches(userRsetPassword.getPassword(), consumerUserValidate.getPayPassword())) {
				int count = RedisUtil.increment_ERROR_PAY_PASSWORD_COUNT(consumerUserValidate.getUserId(), Calendar.getInstance().getTime());
				return new ResponseRestEntity<ConsumerUser>(CommonFun.errorPayPassword(count), "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PAY_PASSWORD_COUNT(consumerUserValidate.getUserId(), Calendar.getInstance().getTime());
		}

		boolean isPass = CommonFun.checkPayPassword(userRsetPassword.getNewPassword());
		if (!isPass) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PASSWORD_PAY_FORMAT_ERROR, "pay password must 6 length.and must number");
		}
		// 修改支付密码
		consumerUserValidate.setPayPassword(encoder.encode(userRsetPassword.getConfirmPassword()));
		consumerUserValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.NOT_FIRST);
		consumerUserValidate.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.NOT_DEFUALT);
		consumerUserService.updateByPrimaryKeySelective(consumerUserValidate);
		consumerUserValidate.setPassword("");
		consumerUserValidate.setPayPassword("");
		consumerUserValidate.setEmail(CommonFun.getEmailWhithStar(consumerUserValidate.getEmail()));

		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPAYPWD, consumerUserValidate, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(consumerUserValidate, HttpRestStatus.OK);

	}

	// 重置密码
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "Reset users password to default", notes = "")
	@RequestMapping(value = "/reset/defaultPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> resetDefaultPassword(@PathVariable("id") String id, @Valid @RequestBody ConsumerUser consumerUser) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 校验旧密码的正确性
		ConsumerUser consumerUserValidate = consumerUserService.selectByPrimaryKey(id);
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		// 修改密码
		consumerUserValidate.setPassword(encoder.encode(consumerUser.getPassword()));
		consumerUserValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		consumerUserValidate.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		consumerUserService.updateByPrimaryKeySelective(consumerUserValidate);
		consumerUserValidate.setPassword(null);
		consumerUserValidate.setPayPassword(null);
		consumerUserValidate.setEmail(CommonFun.getEmailWhithStar(consumerUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPWD, consumerUserValidate, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(consumerUserValidate, HttpRestStatus.OK);
	}

	// 重置支付密码
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "Reset users pay assword to default", notes = "")
	@RequestMapping(value = "/reset/defaultPayPassword/{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> resetDefaultPayPassword(@PathVariable("id") String id, @Valid @RequestBody ConsumerUser consumerUser) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// 校验旧密码的正确性
		ConsumerUser consumerUserValidate = consumerUserService.selectByPrimaryKey(id);
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		// 修改支付密码
		consumerUserValidate.setIsFirstLogin(MessageDef.FIRST_LOGIN.FIRST);
		consumerUserValidate.setPayPassword(encoder.encode(consumerUser.getPayPassword()));
		consumerUserValidate.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.DEFUALT);
		consumerUserService.updateByPrimaryKeySelective(consumerUserValidate);
		consumerUserValidate.setPassword(null);
		consumerUserValidate.setPayPassword(null);
		consumerUserValidate.setEmail(CommonFun.getEmailWhithStar(consumerUserValidate.getEmail()));
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_RESETPAYPWD, consumerUserValidate, CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUser>(consumerUserValidate, HttpRestStatus.OK);
	}

	///////////////////////////////////////////////// uplaod///User//Img//Start//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@PreAuthorize("hasRole('R_BUYER_E') or hasRole('CONSUMER')")
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
			String destPath = REL_FOLDER + MessageDef.UPLOAD_PATH.CONSUMER;
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
				x = x < 0 ? 0 : x;
				y = y < 0 ? 0 : y;
				w = w < 0 ? 0 : w;
				h = h < 0 ? 0 : h;
				cutImage(srcFile, destPath, x, y, w, h);
				int IMAGE_UPLOAD_MIN_WITH = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.IMAGE_UPLOAD_MIN_WITH);
				int IMAGE_UPLOAD_MIN_HEIGHT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.IMAGE_UPLOAD_MIN_HEIGHT);

				if (w < IMAGE_UPLOAD_MIN_WITH || h < IMAGE_UPLOAD_MIN_HEIGHT) {// 报错修改，图片上传剪裁过小2017-11-28 Chen
					return new ResponseRestEntity<Picture>(headers, HttpRestStatus.IMAGE_UPLOAD_SIZE_SMALL_ERROR, localeMessageSourceService.getMessage("common.img.upload.typeFailed"));
				}
			} else {
				Path path2 = Paths.get(destPath);
				Files.write(path2, bytes);
			}

			srcFile.delete();
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + tmpFileName + "'");

			ConsumerUser existConsumerUser = consumerUserService.selectByPrimaryKey(id);
			deleteOldImgFile(existConsumerUser);// 删除旧的头像图片
			ConsumerUser consumerUser = new ConsumerUser();
			consumerUser.setUserId(id);
			consumerUser.setUserImgUrl(userImgUrl);
			consumerUserService.updateByPrimaryKeySelective(consumerUser);
			// 修改日志
			consumerUser.setPassword("");
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, consumerUser, CommonLogImpl.CONSUMER);
			Picture pic = new Picture();
			pic.setPicName(userImgUrl);

			return new ResponseRestEntity<Picture>(pic, headers, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.upload.success"));

		} catch (Exception e) {
			log.error("ConsumerUserController.singleFileUpload exception", e);
			return new ResponseRestEntity<Picture>(headers, HttpRestStatus.IMAGE_UPLOAD_FAILED_ERROR, localeMessageSourceService.getMessage("common.img.unload.fail"));
		}
		// return new ResponseRestEntity<Picture>(headers, HttpRestStatus.UNKNOWN, localeMessageSourceService.getMessage("common.upload.fail"));
	}

	private boolean deleteOldImgFile(ConsumerUser existConsumerUser) {
		if (existConsumerUser != null && existConsumerUser.getUserImgUrl() != null) {
			File userImgFile = new File(existConsumerUser.getUserImgUrl());
			if (userImgFile.exists() && userImgFile.isFile()) {
				if (userImgFile.delete()) {
					return true;
				} else {
					log.error("userImgFile: " + existConsumerUser.getUserImgUrl() + " delete failed with unknow error.");
				}
			}
		}
		return false;
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

	private static void cutImage(File src, String dest, int x, int y, int w, int h) throws IOException {
		Iterator<?> iterator = ImageIO.getImageReadersByFormatName(MessageDef.IMAGETYPE.JPG);
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		File destFile = new File(dest);
		ImageIO.write(bi, MessageDef.IMAGETYPE.JPG, destFile);
		iis.close();
		in.close();
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
			File readPath = new File(READ_FOLDER + MessageDef.UPLOAD_PATH.CONSUMER);
			if (!readPath.exists() && !readPath.isDirectory()) {
				readPath.mkdir();
			}
			File imgFile = new File(READ_FOLDER + MessageDef.UPLOAD_PATH.CONSUMER + "/" + path);
			if (!imgFile.exists() || imgFile.isDirectory()) {
				return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
			}

			InputStreamResource resource = new InputStreamResource(new FileInputStream(READ_FOLDER + MessageDef.UPLOAD_PATH.CONSUMER + "/" + path));
			return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("image/jpeg")).body(resource);
		} catch (Exception e) {
			log.error("", e);
		}

		return new ResponseEntity<InputStreamResource>(HttpStatus.NOT_FOUND);
	}

	///////////////////////////////////////////////// get///User//Img//End//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 查询余额
	@PreAuthorize("hasRole('R_BUYER_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query Balance", notes = "")
	@RequestMapping(value = "/queryBalance", method = RequestMethod.GET)
	public ResponseRestEntity<QueryConsumerBalanceVo> queryConsumerBalance(@RequestParam("userName") String userName) {
		if (null == userName || "".equals(userName)) {
			return new ResponseRestEntity<QueryConsumerBalanceVo>(HttpRestStatus.NOTEMPTY, localeMessageSourceService.getMessage("common.username.empty"));
		}

		QueryConsumerBalanceVo queryBalance = new QueryConsumerBalanceVo();
		queryBalance.setUserName(userName);

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(consumerUserService, userName);

		queryBalance.setBalance(balance);

		return new ResponseRestEntity<QueryConsumerBalanceVo>(queryBalance, HttpRestStatus.OK);

	}

	@PreAuthorize("hasRole('R_BUYER_E')")
	@RequestMapping(value = "/singleUpload", method = RequestMethod.POST)
	public Map<String, Object> singleFileUpload(HttpServletRequest request, @RequestParam("name") String name) throws Exception {
		String fileName = "UPLOAD_BUYER_ERROR.txt";

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
					String status = null;
					String name1 = null;
					String name2 = null;
					String lastName1 = null;
					String lastName2 = null;
					String sex = null;
					String dateOfBirth = null;
					String familyId = null;
					String communityCode = null;
					String clapSeqNo = null;
					String clapNo = null;
					String ClapStoreNo = null;
					if (str.length == 13) {
						idNumber = str[0];
						status = str[1];
						name1 = str[2];
						name2 = str[3];
						lastName1 = str[4];
						lastName2 = str[5];
						sex = str[6];
						dateOfBirth = str[7];

						familyId = str[8];
						communityCode = str[9];
						clapSeqNo = str[10];
						clapNo = str[11];
						ClapStoreNo = str[12];

						if (!CommonFun.isEmpty(idNumber)) {
							idNumber = idNumber.trim();

							// 为了解决txt中的第一行的问题 start
							// 例如第一行数据是1按照常规来看长度应该是1但是长度显示是2 故需要去除第一位得到后面的数据才是真实的数据
							if (line_num == 1 && idNumber.length() >= 2) {
								idNumber = ImportUtil.getValueForUTF_8(idNumber);
							}
							// 为了解决txt中的第一行的问题 end

							String userId = MessageDef.USER_TYPE.CONSUMER_STRING + idNumber;
							String names = name1 + " " + name2 + " " + lastName1 + " " + lastName2;
							ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(userId);
							if (consumerUser == null) {// 没有则新增
								try {// 如果抛异常，则放弃本次操作。循环下一个。
										// 先插入消费用户表
									ConsumerUser consumerUserTmp = new ConsumerUser();
									consumerUserTmp.setUserId(userId);
									consumerUserTmp.setUserName(names);
									// 后台添加字段 start

									consumerUserTmp.setBalance(0);// 余额为0
									consumerUserTmp.setStatus(0);// 状态为启用
									// 密码
									BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
									// merchantUser.setPassword(encoder.encode(DEFAULT_PASSWORD));
									// merchantUser.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD));
									if (clapNo != null) {
										/* String number= idNumber.replaceAll("[^0-9]+",""); */
										// 登陆密码
										String pwd = CommonFun.generaPassword(clapNo, 6);
										consumerUserTmp.setPassword(encoder.encode(pwd));
										// 支付密码
										String number = clapNo.replaceAll("[^0-9]+", "");
										String paypwd = CommonFun.generaPassword(number, 6);
										consumerUserTmp.setPayPassword(encoder.encode(paypwd));
									} else {
										consumerUserTmp.setPassword(encoder.encode(DEFAULT_PASSWORD));
										consumerUserTmp.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD));
									}
									consumerUserTmp.setIsLocked(0);// 未冻结
									consumerUserTmp.setIsBindClap(1);// 已绑定
									consumerUserTmp.setIsBindBankCard(0);// 未绑定
									consumerUserTmp.setIsFirstLogin(1);// 首次登陆
									consumerUserTmp.setCreateTime(PageHelperUtil.getCurrentDate());
									consumerUserTmp.setIsActive(0);
									consumerUserTmp.setIsDefaultPassword(1);
									consumerUserTmp.setIsDefaultPayPassword(1);
									consumerUserTmp.setEmailBindStatus(0);
									// 后台添加字段 end

									// 再插入消费用户clap卡
									ConsumerUserClap consumerUserClapTmp = new ConsumerUserClap();
									consumerUserClapTmp.setConsumerUserClapId(userId);
									consumerUserClapTmp.setUserId(userId);
									consumerUserClapTmp.setIdNumber(idNumber);
									int b = Integer.valueOf(status).intValue();
									consumerUserClapTmp.setStatus(b);
									consumerUserClapTmp.setName1(name1);
									consumerUserClapTmp.setName2(name2);
									consumerUserClapTmp.setLastName1(lastName1);
									consumerUserClapTmp.setLastName2(lastName2);
									int a;
									if (sex.equals("f")) {
										a = 1;
									} else {
										a = 2;
									}
									consumerUserClapTmp.setSex(a);
									// 出生日期
									SimpleDateFormat sim = new SimpleDateFormat("dd/MM/yyyy");
									Date d = sim.parse(dateOfBirth);
									consumerUserClapTmp.setDatebirth(d);
									consumerUserClapTmp.setCommunityCode(communityCode);

									consumerUserClapTmp.setFamilyId(familyId);
									consumerUserClapTmp.setClapSeqNo(clapSeqNo);
									consumerUserClapTmp.setClapNo(clapNo);
									consumerUserClapTmp.setBindTime(PageHelperUtil.getCurrentDate());

									ConsumerFamily consumerFamilyTmp = new ConsumerFamily();
									ConsumerFamily consumerFamily = consumerFamilyService.selectByPrimaryKey(familyId);
									if (consumerFamily == null) {
										consumerFamilyTmp.setFamilyId(familyId);
										consumerFamilyTmp.setName(familyId);
										consumerFamilyTmp.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
										consumerFamilyService.insert(consumerFamilyTmp);

									} else {
										consumerFamily.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
										consumerFamilyService.updateByPrimaryKeySelective(consumerFamily);
									}

									ConsumerRoleUserKey consumerRoleUserKeyTmp = new ConsumerRoleUserKey();
									consumerRoleUserKeyTmp.setUserId(userId);
									consumerRoleUserKeyTmp.setRoleId("1");

									consumerRoleUserService.insert(consumerRoleUserKeyTmp);

									consumerUserService.insert(consumerUserTmp);
									consumerUserClapService.insert(consumerUserClapTmp);
									// 新增日志
									consumerUserTmp.setPassword("");
									consumerUserTmp.setPayPassword("");
									CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, consumerUserTmp, CommonLogImpl.CONSUMER);
									import_int = import_int + 1;
									successFlag = true;
								} catch (Exception e) {
									log.error("", e);
								}

							}
						}

					}
				}
				// 上传失败错误记录
				if (!successFlag) {

					// System.out.println(s);
					try {
						String name = "UPLOAD_BUYER_ERROR.txt";
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

	@ApiOperation(value = "download errorLog", notes = "")
	@RequestMapping(value = "/get/errorLog", method = RequestMethod.GET)
	public void hello(HttpServletResponse res) throws IOException {

		String fileName = "UPLOAD_BUYER_ERROR.txt";
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
		res.setHeader("Content-Disposition", "attachment;filename=" + "UPLOAD_BUYER_ERROR.txt");
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

	// 二维码
	@PreAuthorize("hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Reset users pay assword to default", notes = "")
	@RequestMapping(value = "/getQRCodeByConsumer/{id}", method = RequestMethod.GET)
	public ResponseRestEntity<String> getQRCodeByConsumer(@PathVariable("id") String id) {

		// 校验旧密码的正确性
		ConsumerUser consumerUserValidate = consumerUserService.selectByPrimaryKey(id);
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<String>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		}
		String qrcode = null;
		int count = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.QRCODE_WHILE_COUNT_BREAK);
		while (count > 0) {
			count--;
			qrcode = generQRCode();
			if (RedisUtil.hasKey_QR_CODE(qrcode)) {
				qrcode = null;
				continue;
			}
			break;
		}

		if (qrcode == null || qrcode.isEmpty()) {
			return new ResponseRestEntity<String>(HttpRestStatus.QRCODE_ERROR, localeMessageSourceService.getMessage("common.twodimensional.failed"));
		}
		ConsumerUserQRCode consumerUserQRCode = new ConsumerUserQRCode();
		consumerUserQRCode.setBarCode(qrcode);
		consumerUserQRCode.setUserId(consumerUserValidate.getUserId());
		consumerUserQRCode.setUserName(consumerUserValidate.getUserName());

		List<ConsumerUserClap> consumerUserClapList = consumerUserClapService.selectByUserId(consumerUserValidate.getUserId());
		if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
			consumerUserQRCode.setIdNumer(consumerUserClapList.get(0).getIdNumber());
		}

		RedisUtil.setQR_CODE(qrcode, consumerUserQRCode);

		return new ResponseRestEntity<String>(qrcode, HttpRestStatus.OK);
	}

	// 二维码
	@PreAuthorize("hasRole('MERCHANT')")
	@ApiOperation(value = "Reset users pay assword to default", notes = "")
	@RequestMapping(value = "/getConsumerByQRCode/{qrcode}", method = RequestMethod.GET)
	public ResponseRestEntity<ConsumerUserQRCode> getConsumerByQRCode(@PathVariable("qrcode") String qrcode, @RequestParam(required = false) String clapSeqNo, @RequestParam(required = false) String merchantUserId) {
		if (merchantUserId == null || merchantUserId.length() == 0) {
			return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.CONSUMER_NOTIN_MERCHANT_STORE, "consumer not in merchant store");
		}
		if (CommonFun.isVid(qrcode)) {// qrcode.contains("V") || qrcode.contains("E")
			qrcode = CommonFun.getRelVid(qrcode);
			if (StringUtils.isEmpty(clapSeqNo)) {
				return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.PATRIMONY_CARD_IS_EXPIRED, "爱国卡已过期");
			}
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(qrcode);
			if (consumerUserClap != null) {
				if (!clapSeqNo.equals(consumerUserClap.getClapSeqNo())) {
					return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.PATRIMONY_CARD_IS_EXPIRED, "爱国卡已过期");
				}
				ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(consumerUserClap.getUserId());
				if (consumerUser != null) {
					MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(merchantUserId);
					if (merchantUser != null && merchantUser.getClapStoreNo() != null && merchantUser.getClapStoreNo().equals(consumerUserClap.getClapStoreNo())) {
						ConsumerUserQRCode consumerUserQRCode = new ConsumerUserQRCode();
						consumerUserQRCode.setBarCode("111111111111111111");
						consumerUserQRCode.setUserId(consumerUser.getUserId());
						consumerUserQRCode.setUserName(consumerUser.getUserName());
						consumerUserQRCode.setIdNumer(consumerUserClap.getIdNumber());
						consumerUserQRCode.setIsDefaultPassword(consumerUser.getIsDefaultPassword());
						consumerUserQRCode.setIsDefaultPayPassword(consumerUser.getIsDefaultPayPassword());
						return new ResponseRestEntity<ConsumerUserQRCode>(consumerUserQRCode, HttpRestStatus.OK);
					} else {
						return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.CONSUMER_NOTIN_MERCHANT_STORE, "consumer not in merchant store");
					}
				}
			}
			return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.QRCODE_ERROR, localeMessageSourceService.getMessage("common.twodimensional.failed"));

		} else {
			if (RedisUtil.hasKey_QR_CODE(qrcode)) {
				ConsumerUserQRCode result = RedisUtil.get_QR_CODE(qrcode);
				if (result == null) {
					return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.QRCODE_ERROR, localeMessageSourceService.getMessage("common.twodimensional.failed"));
				}
				List<ConsumerUserClap> list = consumerUserClapService.selectByUserId(result.getUserId());
				if (list != null && list.size() == 1) {
					ConsumerUserClap consumerUserClap = list.get(0);
					MerchantUser merchantUser = merchantUserService.selectByUserId(merchantUserId);
					if (merchantUser != null && merchantUser.getClapStoreNo() != null && merchantUser.getClapStoreNo().equals(consumerUserClap.getClapStoreNo())) {
						return new ResponseRestEntity<ConsumerUserQRCode>(result, HttpRestStatus.OK);
					}
				}
				return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.CONSUMER_NOTIN_MERCHANT_STORE, "consumer not in merchant store");
			}
		}

		return new ResponseRestEntity<ConsumerUserQRCode>(HttpRestStatus.QRCODE_EXPRIE, localeMessageSourceService.getMessage("common.twodimensional.outofdate"));
	}

	// 校验支付密码
	@PreAuthorize("hasRole('MERCHANT')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/validate/paypassword", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> validatePaypassword(@RequestParam("id") String id, @RequestParam("password") String password) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		// 校验旧密码的正确性
		ConsumerUser consumerUserValidate = null;
		if (id != null && !id.isEmpty()) {
			consumerUserValidate = consumerUserService.selectByPrimaryKey(id);
		}
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (MessageDef.STATUS.ENABLE_INT != consumerUserValidate.getStatus()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}

			if (MessageDef.LOCKED.UNLOCKED != consumerUserValidate.getIsLocked()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("common.user.islocked"));
			}

			// 支付密码输入错误次数校验
			if (RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(id, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日支付密码错误次数限制");
			}

			if (!encoder.matches(password, consumerUserValidate.getPayPassword())) {
				int count = RedisUtil.increment_ERROR_PAY_PASSWORD_COUNT(id, Calendar.getInstance().getTime());
				return new ResponseRestEntity<ConsumerUser>(CommonFun.errorPayPassword(count), "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PAY_PASSWORD_COUNT(id, Calendar.getInstance().getTime());
		}

		consumerUserValidate.setPassword("");
		consumerUserValidate.setPayPassword("");
		consumerUserValidate.setEmail(CommonFun.getEmailWhithStar(consumerUserValidate.getEmail()));
		return new ResponseRestEntity<ConsumerUser>(consumerUserValidate, HttpRestStatus.OK);
	}

	// 绑定邮箱
	@PreAuthorize("hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/bindEmail", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> bindEmail(@RequestParam("userId") String userId, @RequestParam("email") String email) {

		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(email)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid and email not empty");
		}
		if (!CommonFun.isEmail(email)) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_FORMAT_ERROR, "email format error");
		}

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(userId);
		if (consumerUser == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		}

		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_BIND_SUBJECT);
		String vCode = CommonFun.getVcode();
		String content = CommonFun.getBindEmailContent(consumerUser.getUserName(), vCode, email);
		RedisUtil.setEmailVCode(userId, vCode);
		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, subject, email, null, null, content);
			return new ResponseRestEntity<>(HttpRestStatus.OK);
		} catch (Exception e) {
			log.error("send email error", e);
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_SEND_ERROR, "send email is error,please try again later.");
		}
	}

	// 绑定邮箱验证验证码
	@PreAuthorize("hasRole('CONSUMER')")
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
		ConsumerUser consumerUser = new ConsumerUser();
		consumerUser.setUserId(userId);
		consumerUser.setEmail(email);
		consumerUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.BIND);
		consumerUserService.updateByPrimaryKeySelective(consumerUser);

		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 绑定邮箱,不再提醒
	@PreAuthorize("hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/notRemind", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> notRemind(@RequestParam("userId") String userId) {

		if (StringUtils.isEmpty(userId)) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid not empty");
		}

		ConsumerUser consumerUser = new ConsumerUser();
		consumerUser.setUserId(userId);
		consumerUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.NOT_REMIND);
		consumerUserService.updateByPrimaryKeySelective(consumerUser);

		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 通过vid查询用户信息
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/vid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerUser> selectByVid(@RequestParam("vid") String vid) {
		vid = CommonFun.getRelVid(vid);
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(vid);
		if (consumerUserClap == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(consumerUserClap.getUserId());
		if (consumerUser == null) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.NOT_FOUND);
		}
		consumerUser.setPassword(null);
		consumerUser.setPayPassword(null);

		Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(consumerUserService, consumerUser.getUserId(), consumerUser.getBalance());
		consumerUser.setBalance(balance);

		consumerUser.setEmail(CommonFun.getEmailWhithStar(consumerUser.getEmail()));

		return new ResponseRestEntity<ConsumerUser>(consumerUser, HttpRestStatus.OK);
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

		ConsumerUser consumerUser = consumerUserService.selectByPrimaryKey(userId);
		if (consumerUser == null) {
			return new ResponseRestEntity<>(HttpRestStatus.NOT_FOUND);
		}
		if (!email.equals(consumerUser.getEmail())) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_NOT_MATH_ERROR, "email not math error");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		ConsumerUser consumerUserTmp = new ConsumerUser();
		consumerUserTmp.setUserId(consumerUser.getUserId());

		String loginPassword = null;
		String payPassword = null;
		if (passwordType.contains(MessageDef.PASSWORD_TYPE.LOGIN_PASSORD)) {
			loginPassword = CommonFun.getLoginPassword();
			consumerUserTmp.setPassword(encoder.encode(loginPassword));
			consumerUserTmp.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		}

		if (passwordType.contains(MessageDef.PASSWORD_TYPE.PAY_PASSWORD)) {
			payPassword = CommonFun.getPayPassword();
			consumerUserTmp.setPayPassword(encoder.encode(payPassword));
			consumerUserTmp.setIsDefaultPayPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.DEFUALT);
		}
		consumerUserService.updateByPrimaryKeySelective(consumerUserTmp);
		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_RETRIEVE_PASSWORD_SUBJECT);
		String content = CommonFun.getRetrievePassordContent(consumerUser.getUserName(), email, loginPassword, payPassword);
		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, subject, email, null, null, content);
			return new ResponseRestEntity<>(HttpRestStatus.OK);
		} catch (Exception e) {
			log.error("sendEmail error", e);
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_SEND_ERROR, "send email is error,please try again later.");
		}
	}

	// 校验登录密码
	@PreAuthorize("hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/validate/loginpassword", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUser> validateLoginpassword(@RequestParam("id") String id, @RequestParam("password") String password) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		ConsumerUser consumerUserValidate = null;
		if (id != null && !id.isEmpty()) {
			consumerUserValidate = consumerUserService.selectByPrimaryKey(id);
		}
		if (null == consumerUserValidate) {
			return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_USER_NOEXIST, localeMessageSourceService.getMessage("common.no.correspondinguser"));
		} else {
			if (MessageDef.STATUS.ENABLE_INT != consumerUserValidate.getStatus()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("common.user.notenabled"));
			}

			if (MessageDef.LOCKED.UNLOCKED != consumerUserValidate.getIsLocked()) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("common.user.islocked"));
			}
			List<ConsumerUserClap> list = consumerUserClapService.selectByUserId(id);
			String username = MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + list.get(0).getIdNumber();

			// 支付密码输入错误次数校验
			if (RedisUtil.islimit_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime())) {
				return new ResponseRestEntity<ConsumerUser>(HttpRestStatus.PAY_PAY_PASSWORD_ERROR_COUNT, "超出每日登录密码错误次数限制");
			}

			if (!encoder.matches(password, consumerUserValidate.getPassword())) {
				int count = RedisUtil.increment_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
				ConsumerUser consumerUser = new ConsumerUser();
				consumerUser.setRemainingTimes(count);
				return new ResponseRestEntity<ConsumerUser>(consumerUser, HttpRestStatus.PASSWORD_ERROR, "密码错误，还可以尝试x次");
			}

			RedisUtil.delete_ERROR_PASSWORD_COUNT(username, Calendar.getInstance().getTime());
		}

		consumerUserValidate.setPassword("");
		consumerUserValidate.setPayPassword("");
		consumerUserValidate.setEmail(CommonFun.getEmailWhithStar(consumerUserValidate.getEmail()));
		return new ResponseRestEntity<ConsumerUser>(consumerUserValidate, HttpRestStatus.OK);
	}

	// 解锁登录密码
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockLoginPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockLoginPassowrd(@RequestParam(required = false) String userId, @RequestParam(required = false) String idNumber) {
		idNumber = CommonFun.getRelVid(idNumber);
		if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(idNumber)) {

			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}

		if (StringUtils.isNotEmpty(userId)) {
			List<ConsumerUserClap> list = consumerUserClapService.selectByUserId(userId);
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + list.get(0).getIdNumber(), Calendar.getInstance().getTime());
		}

		if (StringUtils.isNotEmpty(idNumber)) {
			RedisUtil.delete_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + idNumber, Calendar.getInstance().getTime());
		}
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 解锁支付密码
	@PreAuthorize("hasRole('R_BUYER_E')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/unlockPayPassowrd", method = RequestMethod.PUT)
	public ResponseRestEntity<HttpRestStatus> unlockPayPassowrd(@RequestParam(required = false) String userId) {
		if (StringUtils.isEmpty(userId)) {

			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY);
		}
		RedisUtil.delete_ERROR_PAY_PASSWORD_COUNT(userId, Calendar.getInstance().getTime());
		return new ResponseRestEntity<>(HttpRestStatus.OK);
	}

	// 调取webservice
	// @PreAuthorize("hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/registerGetConsumerInfo", method = RequestMethod.POST)
	public ResponseRestEntity<RegisterResponse> registerGetConsumerInfo(@RequestBody RegisterRequest registerRequest) {
		registerRequest.setIdNumber(CommonFun.getRelVid(registerRequest.getIdNumber()));
		
		if (registerRequest == null || StringUtils.isEmpty(registerRequest.getIdNumber()) || StringUtils.isEmpty(registerRequest.getPatrimonyCardCode())) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "vid and clapCode not empty");
		}

		
	/*	HttpRestStatus imgValidateStatus = getImgValidateStatus(registerRequest);
		if(imgValidateStatus != HttpRestStatus.OK){
			return new ResponseRestEntity<>(imgValidateStatus, "imgValidateStatus not ok");
		}
*/
		ConsumerUserClap clapTmp = consumerUserClapService.selectByIdNumber(registerRequest.getIdNumber());
		if (null != clapTmp) {
			return new ResponseRestEntity<>(HttpRestStatus.USER_CONFLICT, "This consumer is registerd,please to login");
		}

		HttpPost httpPost = new HttpPost(URL_REGISTER_GET_PATRIOT);
		httpPost.setConfig(config);
		APIRequest apiRequest = new APIRequest();
		String[] params = { registerRequest.getIdNumber(), registerRequest.getPatrimonyCardCode() };
		apiRequest.setParams(params);
		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(apiRequest);
		StringEntity stringEntity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json");
		httpPost.setEntity(stringEntity);
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					if (responseStr != null && responseStr.length() > 0) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						APIGetpatriotResponse apiGetpatriotResponse = null;
						if (responseStr.contains("body")) {
							apiGetpatriotResponse = JSONObject.toJavaObject(jsonObjectResponse.getJSONObject("body"), APIGetpatriotResponse.class);
						}else{
							apiGetpatriotResponse = JSONObject.toJavaObject(jsonObjectResponse, APIGetpatriotResponse.class);
						}
						if (apiGetpatriotResponse != null) {
							if (apiGetpatriotResponse.isR_stat()) {

								RegisterResponse registerResponse = new RegisterResponse();
								registerResponse.setIdNumber(registerRequest.getIdNumber());
								registerResponse.setPatrimonyCardCode(registerRequest.getPatrimonyCardCode());
								registerResponse.setImgCode(registerRequest.getImgCode());
								registerResponse.setImgValidateCode(registerRequest.getImgValidateCode());

								registerResponse.setR_cod(apiGetpatriotResponse.getR_cod());
								registerResponse.setR_ser(apiGetpatriotResponse.getR_ser());
								registerResponse.setR_stat(apiGetpatriotResponse.isR_stat());
								registerResponse.setR_ced(apiGetpatriotResponse.getR_ced());
								registerResponse.setR_n1(apiGetpatriotResponse.getR_n1());
								registerResponse.setR_n2(apiGetpatriotResponse.getR_n2());
								registerResponse.setR_ap1(apiGetpatriotResponse.getR_ap1());
								registerResponse.setR_ap2(apiGetpatriotResponse.getR_ap2());
								registerResponse.setR_gen(apiGetpatriotResponse.getR_gen());

								registerResponse.setR_fnac(apiGetpatriotResponse.getR_fnac());
								registerResponse.setR_mail(apiGetpatriotResponse.getR_mail());
								return new ResponseRestEntity<RegisterResponse>(registerResponse, HttpRestStatus.OK);
							} else {
								log.warn(responseStr);
								return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_STATES_NOT_SUCC, "get patriot status not true");
							}
						} else {
							log.warn(responseStr);
							return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_FORMAT_ERROR, "get patriot body object is null");
						}
					} else {
						log.warn(responseStr);
						return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_BODY_NOT_EXIST, "get patriot body is null");
					}
				} else {
					return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_IS_NULL, "get patriot response is null");
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.error("get patriot exception", e);
			return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_FAILD, "get patriot exception");
		} finally {
		}
	}

	// @PreAuthorize("hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/registerMail", method = RequestMethod.PUT)
	public ResponseRestEntity<ResponseRestEntity> registerMail(@RequestBody RegisterRequest registerRequest) {
		registerRequest.setIdNumber(CommonFun.getRelVid(registerRequest.getIdNumber()));
		if (registerRequest == null || StringUtils.isEmpty(registerRequest.getIdNumber()) || StringUtils.isEmpty(registerRequest.getR_mail()) || StringUtils.isEmpty(registerRequest.getLoginPassword())
				|| StringUtils.isEmpty(registerRequest.getPaymentPassword())) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid and email not empty");
		}
		if (!CommonFun.isEmail(registerRequest.getR_mail())) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_FORMAT_ERROR, "email format error");
		}
		
		HttpRestStatus imgValidateStatus = getImgValidateStatus(registerRequest);
//		if(imgValidateStatus != HttpRestStatus.OK){
//			return new ResponseRestEntity<>(imgValidateStatus, "imgValidateStatus not ok");
//		}
		

		String subject = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.EMAIL_BIND_SUBJECT);
		String vCode = CommonFun.getVcode();
		String content = CommonFun.getBindEmailContent(registerRequest.getR_n1() + " " + registerRequest.getR_ap1(), vCode, registerRequest.getR_mail());
		RedisUtil.setEmailVCode(registerRequest.getIdNumber(), vCode);
		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, subject, registerRequest.getR_mail(), null, null, content);
			return new ResponseRestEntity<>(HttpRestStatus.OK);
		} catch (Exception e) {
			log.error("send email error", e);
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_SEND_ERROR, "send email is error,please try again later.");
		}

	}

	// 验证验证码
	// @PreAuthorize("hasRole('CONSUMER')")
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/registerValidateEmailCode", method = RequestMethod.PUT)
	public ResponseRestEntity<RegisterResponse> registerValidateEmailCode(@RequestBody RegisterRequest registerRequest) throws Exception {

		if (registerRequest == null || StringUtils.isEmpty(registerRequest.getIdNumber()) || StringUtils.isEmpty(registerRequest.getR_mail()) || StringUtils.isEmpty(registerRequest.getLoginPassword())
				|| StringUtils.isEmpty(registerRequest.getPaymentPassword()) || StringUtils.isEmpty(registerRequest.getEmailValidateCode())) {
			return new ResponseRestEntity<>(HttpRestStatus.NOTEMPTY, "userid validateCode and email not empty");
		}
		if (!CommonFun.isEmail(registerRequest.getR_mail())) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_FORMAT_ERROR, "email format error");
		}

		String rvalidateCode = RedisUtil.getEmailVCode(registerRequest.getIdNumber());
		if (!registerRequest.getEmailValidateCode().equals(rvalidateCode)) {
			return new ResponseRestEntity<>(HttpRestStatus.EMAIL_VALIDATE_CODE_ERROR, "email validate code error");
		}

		
//		HttpRestStatus imgValidateStatus = getImgValidateStatus(registerRequest);
//		if(imgValidateStatus != HttpRestStatus.OK){
//			return new ResponseRestEntity<>(imgValidateStatus, "imgValidateStatus not ok");
//		}
		
		ConsumerUserClap clapTmp = consumerUserClapService.selectByIdNumber(registerRequest.getIdNumber());
		if (null != clapTmp) {
			return new ResponseRestEntity<>(HttpRestStatus.USER_CONFLICT, "This consumer is registerd,please to login");
		}

		try {
			return consumerUserService.registerSucc(registerRequest, httpClient, config, URL_REGISTER_REGISTER_UPDATE_WALLET);
		} catch (RegisterApiReponseStatesNotSuccException e) {
			return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_STATES_NOT_SUCC, "register_update_wallet status not true");
		} catch (RegisterApiReponseFormatErrorException e) {
			return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_FORMAT_ERROR, "register_update_wallet body object is null");
		} catch (RegisterApiReponseBodyNotExistException e) {
			return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_BODY_NOT_EXIST, "register_update_wallet body is null");
		} catch (RegisterApiReponseIsNullException e) {
			return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_RESPONSE_IS_NULL, "register_update_wallet response is null");
		} catch (RegisterApiFailException e) {
			return new ResponseRestEntity<>(HttpRestStatus.REGISTER_API_FAILD, "register_update_wallet exception");
		} catch (Exception e) {
			throw e;
		}
	}

	// 图片验证码
	@ApiOperation(value = "", notes = "")
	@RequestMapping(value = "/registerImgvalidateCode", method = RequestMethod.POST)
	public ResponseRestEntity<RegisterImgValidateResponse> registerImgvalidateCode(@RequestParam("id") String id, @RequestParam("width") String width, @RequestParam("height") String height) {

		int REGISTER_IMG_VALIDATE_CODE_LEN = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.REGISTER_IMG_VALIDATE_CODE_LEN);
		RegisterImgValidateResponse resp = new RegisterImgValidateResponse();
		Captcha captcha = new GifCaptcha(Integer.valueOf(width), Integer.valueOf(height), REGISTER_IMG_VALIDATE_CODE_LEN);// gif格式动画验证码 150 40 5

		try {
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			captcha.out(bs);
			String outBase64Str = Base64.byteArrayToBase64(bs.toByteArray());
			if (captcha.text() != null) {
				RedisUtil.setVerificationCode(id, captcha.text());
			} else {
				return new ResponseRestEntity<RegisterImgValidateResponse>(resp, HttpRestStatus.GIF_REGISTER_CODE_GET_FAILED);
			}
			resp.setCampo(outBase64Str);

		} catch (Exception e) {
			log.error("validateRegisterCode exception ", e);
		}

		return new ResponseRestEntity<RegisterImgValidateResponse>(resp, HttpRestStatus.OK);
	}

	private static String generQRCode() {
		String qrcode = "";
		Random r = new Random();
		int num = 0;
		int count = 0;
		while (count < 16) {
			int randNum = r.nextInt(10);
			qrcode += randNum;
			count++;
			num += randNum;
		}
		qrcode += ((num % 100) / 10);
		qrcode += (num % 10);
		return qrcode;
	}

	// 校验验证码
	private HttpRestStatus getImgValidateStatus(RegisterRequest registerRequest) {
		String id = registerRequest.getImgCode();
		String inputCode = registerRequest.getImgValidateCode();
		if (StringUtils.isNotEmpty(id)) {
			if (StringUtils.isNotEmpty(inputCode)) {
				String redisValue = RedisUtil.getVerificationCode(id);
				if (redisValue != null) {
					if (!inputCode.equalsIgnoreCase(redisValue)) {
						RedisUtil.deleteRedisKey(id);
						return HttpRestStatus.GIF_VALIDATE_CODE_ERROR;
					}
				} else {
					return HttpRestStatus.GIF_VALIDATE_CODE_EXPIRED;
				}
			} else {
				return HttpRestStatus.GIF_VALIDATE_CODE_INPUT_EMPTY;
			}
		} else {
			return HttpRestStatus.GIF_VALIDATE_CODE_UUID_EMPTY;
		}

		return HttpRestStatus.OK;

	}

	// 根据clapStoreNo查询数据
	@ApiOperation(value = "Query selectByClapStoreNo，Support paging", notes = "")
	@RequestMapping(value = "/clapStoreNo", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUser>> selectByClapStoreNo(@RequestParam(required = false) String clapStoreNo) {
		ConsumerUserClap consumerUserClap = new ConsumerUserClap();
		consumerUserClap.setClapStoreNo(clapStoreNo);

		List<ConsumerUserClap> list = new ArrayList<ConsumerUserClap>();
		list = consumerUserClapService.selectPage(consumerUserClap);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
		} else {
			ConsumerUser consumerUser = new ConsumerUser();
			consumerUser.setUserId(list.get(0).getUserId());
			List<ConsumerUser> ConsumerUserlist = new ArrayList<ConsumerUser>();
			ConsumerUserlist = consumerUserService.selectPage(consumerUser);
			if (ConsumerUserlist == null || ConsumerUserlist.isEmpty()) {
				return new ResponseRestEntity<List<ConsumerUser>>(new ArrayList<ConsumerUser>(), HttpRestStatus.NOT_FOUND);
			}
			List<ConsumerUser> listNew = new ArrayList<ConsumerUser>();
			for (ConsumerUser bean : ConsumerUserlist) {
				ConsumerUserClap consumerUserClap1 = consumerUserClapService.selectByUser(bean.getUserId());
				if (consumerUserClap1 != null) {
					bean.setIdNumber(consumerUserClap1.getIdNumber());
				}
				Double balance = RedisUtil.increment_ACCOUNT_AMOUNT(consumerUserService, consumerUser.getUserId(), bean.getBalance());
				bean.setBalance(balance);

				bean.setLockedPassword(RedisUtil.islimit_ERROR_PASSWORD_COUNT(MessageDef.USER_TYPE.CONSUMER_STRING + RedisDef.DELIMITER.UNDERLINE + consumerUserClap1.getIdNumber(), Calendar.getInstance().getTime()));
				bean.setLockedPayPassword(RedisUtil.islimit_ERROR_PAY_PASSWORD_COUNT(bean.getUserId(), Calendar.getInstance().getTime()));

				bean.setPassword("");
				bean.setPayPassword("");
				bean.setEmail(CommonFun.getEmailWhithStar(bean.getEmail()));

				listNew.add(bean);
			}
			return new ResponseRestEntity<List<ConsumerUser>>(listNew, HttpRestStatus.OK);
		}
	}

}
