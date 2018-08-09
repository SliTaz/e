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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyGroupFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroup;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroupFamilyKey;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.GoodsType;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/consumerFamilyGroupFamily")
@RestController
public class ConsumerFamilyGroupFamilyController {
	
	@Autowired
	ConsumerFamilyGroupFamilyService consumerFamilyGroupFamilyService;
	@Autowired
	ConsumerFamilyService consumerFamilyService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_BUYER_F_G_Q')")
	@ApiOperation(value = "Query ConsumerFamilyGroupFamily, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerFamilyGroupFamilyKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String familyId, 
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		
		ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey = new ConsumerFamilyGroupFamilyKey();
		consumerFamilyGroupFamilyKey.setConsumerFamilyGroupId(id);
		consumerFamilyGroupFamilyKey.setFamilyId(familyId);
		
		
		int count = consumerFamilyGroupFamilyService.count(consumerFamilyGroupFamilyKey);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerFamilyGroupFamilyKey>>(new ArrayList<ConsumerFamilyGroupFamilyKey>(), HttpRestStatus.NOT_FOUND);
		}

		List<ConsumerFamilyGroupFamilyKey> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerFamilyGroupFamilyService.selectPage(consumerFamilyGroupFamilyKey);
		} else {
			list = consumerFamilyGroupFamilyService.selectPage(consumerFamilyGroupFamilyKey);
		}
		List<ConsumerFamilyGroupFamilyKey> listNew = new ArrayList<ConsumerFamilyGroupFamilyKey>();
		for(ConsumerFamilyGroupFamilyKey bean:list){
			ConsumerFamily consumerFamily = consumerFamilyService.selectByPrimaryKey(bean.getFamilyId());
			if(consumerFamily!=null){
				bean.setFamilyName(consumerFamily.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerFamilyGroupFamilyKey>>(listNew, HttpRestStatus.OK, count, count);
	}
	
	
	
	//删除组用户
	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "delete ConsumerFamilyGroupFamily", notes = "")
	@RequestMapping(value = "/deleteConsumerFamilyGroupFamily", method = RequestMethod.GET)
	public ResponseRestEntity<Void> deleteConsumerFamilyGroupFamily(@RequestParam(required = false) String id,@RequestParam(required = false) String familyId) {
		ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey = new ConsumerFamilyGroupFamilyKey();
		consumerFamilyGroupFamilyKey.setConsumerFamilyGroupId(id);
		consumerFamilyGroupFamilyKey.setFamilyId(familyId);
		
		consumerFamilyGroupFamilyService.deleteConsumerFamilyGroupFamily(consumerFamilyGroupFamilyKey);
		
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}
	
	

	@PreAuthorize("hasRole('R_BUYER_F_G_E')")
	@ApiOperation(value = "Add ConsumerFamilyGroupFamily", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createGoodsType(@Valid @RequestBody ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey, BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		List<ConsumerFamilyGroupFamilyKey> list = consumerFamilyGroupFamilyService.selectPage(consumerFamilyGroupFamilyKey);
		if (list!=null&&list.size()>0) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		
		consumerFamilyGroupFamilyService.insert(consumerFamilyGroupFamilyKey);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerFamilyGroupFamily/{id}").buildAndExpand(consumerFamilyGroupFamilyKey.getFamilyId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}
	
}
