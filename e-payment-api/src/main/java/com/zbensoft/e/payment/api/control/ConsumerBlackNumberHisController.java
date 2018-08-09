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
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.db.domain.ConsumerBlackNumberHis;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerBlackNumberHis")
@RestController
public class ConsumerBlackNumberHisController {
	@Autowired
	ConsumerBlackNumberHisService consumerBlackNumberHisService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_BUYER_B_N_H_Q')")
	@ApiOperation(value = "Query ConsumerBlackNumberHis，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerBlackNumberHis>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId,
			
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		userId = CommonFun.getRelVid(userId);
		ConsumerBlackNumberHis consumerBlackNumberHis = new ConsumerBlackNumberHis();
		// 输入idNumber查询
		if ((userId == null || "".equals(userId)) && (id == null || "".equals(id))) {
			return new ResponseRestEntity<List<ConsumerBlackNumberHis>>(new ArrayList<ConsumerBlackNumberHis>(),
					HttpRestStatus.NOT_FOUND);
		}
		consumerBlackNumberHis.setConsumerBlackNumberHisId(id);
		consumerBlackNumberHis.setUserId(userId);
	
		
		int count = consumerBlackNumberHisService.count(consumerBlackNumberHis);
		if (count == 0) {
			return new ResponseRestEntity<List<ConsumerBlackNumberHis>>(new ArrayList<ConsumerBlackNumberHis>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerBlackNumberHis> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerBlackNumberHisService.selectPage(consumerBlackNumberHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerBlackNumberHisService.selectPage(consumerBlackNumberHis);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerBlackNumberHis>>(new ArrayList<ConsumerBlackNumberHis>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ConsumerBlackNumberHis>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_H_Q')")
	@ApiOperation(value = "Query ConsumerBlackNumberHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerBlackNumberHis> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerBlackNumberHis consumerBlackNumberHis = consumerBlackNumberHisService.selectByPrimaryKey(id);
		if (consumerBlackNumberHis == null) {
			return new ResponseRestEntity<ConsumerBlackNumberHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerBlackNumberHis>(consumerBlackNumberHis, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_H_E')")
	@ApiOperation(value = "Add ConsumerBlackNumberHis", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createConsumerBlackNumberHis(@Valid @RequestBody ConsumerBlackNumberHis consumerBlackNumberHis,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(CommonFun.getRelVid(consumerBlackNumberHis.getUserId()));//VID
		if(consumerUserClap==null){
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		
		consumerBlackNumberHis.setConsumerBlackNumberHisId(IDGenerate.generateCommTwo(IDGenerate.CONSUMER_BLACK_NUMBER_HIS));
		consumerBlackNumberHis.setCreateTime(PageHelperUtil.getCurrentDate());
		consumerBlackNumberHis.setDeleteTime(PageHelperUtil.getCurrentDate());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerBlackNumberHisService.insert(consumerBlackNumberHis);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerBlackNumberHis,CommonLogImpl.CONSUMER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerBlackNumberHis/{id}").buildAndExpand(consumerBlackNumberHis.getConsumerBlackNumberHisId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_H_E')")
	@ApiOperation(value = "Edit ConsumerBlackNumberHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerBlackNumberHis> updateConsumerBlackNumberHis(@PathVariable("id") String id,@Valid @RequestBody ConsumerBlackNumberHis consumerBlackNumberHis, BindingResult result) {

		ConsumerBlackNumberHis currentConsumerBlackNumberHis = consumerBlackNumberHisService.selectByPrimaryKey(id);

		if (currentConsumerBlackNumberHis == null) {
			return new ResponseRestEntity<ConsumerBlackNumberHis>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentConsumerBlackNumberHis.setUserId(consumerBlackNumberHis.getUserId());
		currentConsumerBlackNumberHis.setCreateReason(consumerBlackNumberHis.getCreateReason());

		currentConsumerBlackNumberHis.setDeleteReason(consumerBlackNumberHis.getDeleteReason());
		currentConsumerBlackNumberHis.setRemark(consumerBlackNumberHis.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerBlackNumberHis>(currentConsumerBlackNumberHis,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerBlackNumberHisService.updateByPrimaryKey(currentConsumerBlackNumberHis);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerBlackNumberHis,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerBlackNumberHis>(currentConsumerBlackNumberHis, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_H_E')")
	@ApiOperation(value = "Edit Part ConsumerBlackNumberHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerBlackNumberHis> updateConsumerBlackNumberHisSelective(@PathVariable("id") String id,
			@RequestBody ConsumerBlackNumberHis consumerBlackNumberHis) {

		ConsumerBlackNumberHis currentConsumerBlackNumberHis = consumerBlackNumberHisService.selectByPrimaryKey(id);

		if (currentConsumerBlackNumberHis == null) {
			return new ResponseRestEntity<ConsumerBlackNumberHis>(HttpRestStatus.NOT_FOUND);
		}
		consumerBlackNumberHis.setConsumerBlackNumberHisId(id);
		consumerBlackNumberHisService.updateByPrimaryKeySelective(consumerBlackNumberHis);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerBlackNumberHis,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerBlackNumberHis>(currentConsumerBlackNumberHis, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_B_N_H_E')")
	@ApiOperation(value = "Delete ConsumerBlackNumberHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerBlackNumberHis> deleteConsumerBlackNumberHis(@PathVariable("id") String id) {

		ConsumerBlackNumberHis consumerBlackNumberHis = consumerBlackNumberHisService.selectByPrimaryKey(id);
		if (consumerBlackNumberHis == null) {
			return new ResponseRestEntity<ConsumerBlackNumberHis>(HttpRestStatus.NOT_FOUND);
		}

		consumerBlackNumberHisService.deleteByPrimaryKey(id);
		//删除日志开始
				ConsumerBlackNumberHis consumer = new ConsumerBlackNumberHis();
				consumer.setConsumerBlackNumberHisId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer,CommonLogImpl.CONSUMER);
			//删除日志结束
		return new ResponseRestEntity<ConsumerBlackNumberHis>(HttpRestStatus.NO_CONTENT);
	}
	//批量
	@PreAuthorize("hasRole('R_BUYER_B_N_H_E')")
	@ApiOperation(value = "Delete Many MerchantBlackNumberHiss", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerBlackNumberHis> deleteConsumerBlackNumberHisMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        		consumerBlackNumberHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<ConsumerBlackNumberHis>(HttpRestStatus.NO_CONTENT);
	}
}