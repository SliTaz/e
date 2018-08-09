package com.zbensoft.e.payment.api.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantGroupUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.MerchantGroupUserKey;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/merchantGroupUser")
@RestController
public class MerchantGroupUserController {
	
	@Autowired
	MerchantGroupUserService merchantGroupUserService;
	@Autowired
	MerchantUserService merchantUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_SELLER_G_Q')")
	@ApiOperation(value = "Query MerchantGroupUser, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantGroupUserKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId, 
			@RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		MerchantGroupUserKey merchantGroupUserKey = new MerchantGroupUserKey();
		// 必须输入一个进行查询
		if ((idNumber == null || "".equals(idNumber))  && (userId == null || "".equals(userId))) {
			return new ResponseRestEntity<List<MerchantGroupUserKey>>(new ArrayList<MerchantGroupUserKey>(), HttpRestStatus.NOT_FOUND);
		}
		if (idNumber == null || "".equals(idNumber)) {
			merchantGroupUserKey.setUserId(userId);
		} else {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(idNumber);
			if (merchantUser == null) {
				return new ResponseRestEntity<List<MerchantGroupUserKey>>(new ArrayList<MerchantGroupUserKey>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (userId == null || "".equals(userId)) {
					merchantGroupUserKey.setUserId(merchantUser.getUserId());
				} else {
					if (userId.equals(merchantUser.getUserId())) {
						merchantGroupUserKey.setUserId(userId);
					} else {
						return new ResponseRestEntity<List<MerchantGroupUserKey>>(new ArrayList<MerchantGroupUserKey>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}

		}
		
		merchantGroupUserKey.setMerchantGroupId(id);
	
		
		
		int count = merchantGroupUserService.count(merchantGroupUserKey);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantGroupUserKey>>(new ArrayList<MerchantGroupUserKey>(), HttpRestStatus.NOT_FOUND);
		}

		List<MerchantGroupUserKey> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantGroupUserService.selectPage(merchantGroupUserKey);
		} else {
			list = merchantGroupUserService.selectPage(merchantGroupUserKey);
		}
		List<MerchantGroupUserKey> listNew = new ArrayList<MerchantGroupUserKey>();
		for(MerchantGroupUserKey bean:list){
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(bean.getUserId());
			if(merchantUser!=null){
				bean.setIdNumber(merchantUser.getIdNumber());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<MerchantGroupUserKey>>(listNew, HttpRestStatus.OK, count, count);
	}
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "Add merchatGroupUser", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@RequestBody MerchantGroupUserKey merchantGroupUserKey,BindingResult result, UriComponentsBuilder ucBuilder) {
		//consumerGroup.setConsumerGroupId(System.currentTimeMillis() + "");
	/*	consumerGroup.setConsumerGroupId(IDGenerate.generateCONSUMER_USER_ID());*/

		merchantGroupUserKey.setIdNumber(CommonFun.getRelVid(merchantGroupUserKey.getIdNumber()));
		//可以输入clapStoreNo进行新增
		if ("userId".equals(merchantGroupUserKey.getUserId())) {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(merchantGroupUserKey.getIdNumber());
			if (merchantUser != null) {
				merchantGroupUserKey.setUserId(merchantUser.getUserId());
			}
			else{
				return new ResponseRestEntity<Void>(HttpRestStatus.MERCHANT_NOT_FOUND,localeMessageSourceService.getMessage("common.create.conflict.message"));
			}
		}
		
		else{
			MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(merchantGroupUserKey.getUserId());
			if (merchantUser != null) {
				merchantGroupUserKey.setUserId(merchantUser.getUserId());
			}
			else{
				return new ResponseRestEntity<Void>(HttpRestStatus.USERID_NOT_FOUND,localeMessageSourceService.getMessage("common.create.conflict.message"));
			}
		}
		
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				List<MerchantGroupUserKey> list = merchantGroupUserService.selectPage(merchantGroupUserKey);
				if (list!=null&&list.size()>0) {
					return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
				}
         //新增
				merchantGroupUserService.insert(merchantGroupUserKey);
         //返回处理
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantGroupUser/{id}").buildAndExpand(merchantGroupUserKey.getMerchantGroupId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}
	
	//删除组用户
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "deleteMerchantGroupUser", notes = "")
	@RequestMapping(value = "/deleteMerchantGroupUser", method = RequestMethod.GET)
	public ResponseRestEntity<Void> deleteMerchantGroupUser(@RequestParam(required = false) String id,@RequestParam(required = false) String userId) {
		MerchantGroupUserKey merchantGroupUserKey = new MerchantGroupUserKey();
		merchantGroupUserKey.setMerchantGroupId(id);
		merchantGroupUserKey.setUserId(userId);
		
		merchantGroupUserService.deleteMerchantGroupUser(merchantGroupUserKey);
		
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}
	
	
}
