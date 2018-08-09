package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerBlackNumberHisService;
import com.zbensoft.e.payment.api.service.api.ConsumerBlackNumberService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.db.domain.ConsumerBlackNumber;
import com.zbensoft.e.payment.db.domain.ConsumerBlackNumberHis;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerBlackNumber")
@RestController
public class ConsumerBlackNumberController {
	@Autowired
	ConsumerBlackNumberService consumerBlackNumberService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Autowired
	ConsumerBlackNumberHisService consumerBlackNumberHisService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_BUYER_B_N_Q')")
	@ApiOperation(value = "Query ConsumerBlackNumber，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerBlackNumber>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String createReason,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		id = CommonFun.getRelVid(id);
		ConsumerBlackNumber consumerBlackNumber = new ConsumerBlackNumber();
		consumerBlackNumber.setUserId(id);
		consumerBlackNumber.setCreateReason(createReason);
		
		int count = consumerBlackNumberService.count(consumerBlackNumber);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerBlackNumber>>(new ArrayList<ConsumerBlackNumber>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerBlackNumber> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerBlackNumberService.selectPage(consumerBlackNumber);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerBlackNumberService.selectPage(consumerBlackNumber);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerBlackNumber>>(new ArrayList<ConsumerBlackNumber>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ConsumerBlackNumber>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_Q')")
	@ApiOperation(value = "Query ConsumerBlackNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerBlackNumber> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerBlackNumber consumerBlackNumber = consumerBlackNumberService.selectByPrimaryKey(id);
		if (consumerBlackNumber == null) {
			return new ResponseRestEntity<ConsumerBlackNumber>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerBlackNumber>(consumerBlackNumber, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_E')")
	@ApiOperation(value = "Add ConsumerBlackNumber", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerBlackNumber(@Valid @RequestBody ConsumerBlackNumber consumerBlackNumber,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(CommonFun.getRelVid(consumerBlackNumber.getUserId()));//VID
		if(consumerUserClap==null){
			return new ResponseRestEntity<Void>(HttpRestStatus.CONSUMER_NOT_FOUND,
					localeMessageSourceService.getMessage("common.select.consumer.message"));
		}
		consumerBlackNumber.setCreateTime(PageHelperUtil.getCurrentDate());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		if (consumerBlackNumberService.isExist(consumerBlackNumber)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		consumerBlackNumber.setUserId(CommonFun.getRelVid(consumerBlackNumber.getUserId()));
		consumerBlackNumberService.insert(consumerBlackNumber);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerBlackNumber,CommonLogImpl.CONSUMER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerBlackNumber/{id}").buildAndExpand(consumerBlackNumber.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_E')")
	@ApiOperation(value = "Edit ConsumerBlackNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerBlackNumber> updateConsumerBlackNumber(@PathVariable("id") String id,@Valid @RequestBody ConsumerBlackNumber consumerBlackNumber, BindingResult result) {

		ConsumerBlackNumber currentConsumerBlackNumber = consumerBlackNumberService.selectByPrimaryKey(id);

		if (currentConsumerBlackNumber == null) {
			return new ResponseRestEntity<ConsumerBlackNumber>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerBlackNumber.setCreateReason(consumerBlackNumber.getCreateReason());

		
		currentConsumerBlackNumber.setRemark(consumerBlackNumber.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerBlackNumber>(currentConsumerBlackNumber,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerBlackNumberService.updateByPrimaryKey(currentConsumerBlackNumber);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerBlackNumber,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerBlackNumber>(currentConsumerBlackNumber, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_E')")
	@ApiOperation(value = "Edit Part ConsumerBlackNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerBlackNumber> updateConsumerBlackNumberSelective(@PathVariable("id") String id,
			@RequestBody ConsumerBlackNumber consumerBlackNumber) {

		ConsumerBlackNumber currentConsumerBlackNumber = consumerBlackNumberService.selectByPrimaryKey(id);

		if (currentConsumerBlackNumber == null) {
			return new ResponseRestEntity<ConsumerBlackNumber>(HttpRestStatus.NOT_FOUND);
		}
		consumerBlackNumber.setUserId(id);
		consumerBlackNumberService.updateByPrimaryKeySelective(consumerBlackNumber);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerBlackNumber,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerBlackNumber>(currentConsumerBlackNumber, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_E')")
	@ApiOperation(value = "Delete ConsumerBlackNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerBlackNumber> deleteConsumerBlackNumber(@PathVariable("id") String id) {

		ConsumerBlackNumber consumerBlackNumber = consumerBlackNumberService.selectByPrimaryKey(id);
		if (consumerBlackNumber == null) {
			return new ResponseRestEntity<ConsumerBlackNumber>(HttpRestStatus.NOT_FOUND);
		}

		consumerBlackNumberService.deleteByPrimaryKey(id);
		//删除日志开始
				ConsumerBlackNumber consumer = new ConsumerBlackNumber();
				consumer.setUserId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer,CommonLogImpl.CONSUMER);
			//删除日志结束
		    	
	//删除在consumerblacknumberhis添加记录
		    	ConsumerBlackNumberHis consumerBlackNumberHis = new ConsumerBlackNumberHis();
		    	consumerBlackNumberHis.setConsumerBlackNumberHisId(IDGenerate.generateCommTwo(IDGenerate.CONSUMER_BLACK_NUMBER_HIS));
		    	consumerBlackNumberHis.setUserId(consumerBlackNumber.getUserId());
		    	consumerBlackNumberHis.setCreateReason(consumerBlackNumber.getCreateReason());
		    	consumerBlackNumberHis.setCreateTime(consumerBlackNumber.getCreateTime());
				consumerBlackNumberHis.setDeleteTime(PageHelperUtil.getCurrentDate());
		    	consumerBlackNumberHisService.insert(consumerBlackNumberHis);
		return new ResponseRestEntity<ConsumerBlackNumber>(HttpRestStatus.NO_CONTENT);
	}

}