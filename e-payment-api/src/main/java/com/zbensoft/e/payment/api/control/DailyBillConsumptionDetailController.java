package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumptionTypeService;
import com.zbensoft.e.payment.api.service.api.DailyBillConsumptionDetailService;
import com.zbensoft.e.payment.db.domain.ConsumptionType;
import com.zbensoft.e.payment.db.domain.DailyBillConsumptionDetail;
import com.zbensoft.e.payment.db.domain.MonthBillConsumptionDetail;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/dailyBillConsumptionDetail")
@RestController
public class DailyBillConsumptionDetailController {
	@Autowired
	DailyBillConsumptionDetailService dailyBillConsumptionDetailService;
	@Autowired
	ConsumptionTypeService consumptionTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query DailyBillConsumptionDetail，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<DailyBillConsumptionDetail>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String billConsumptionTypeId,
			@RequestParam(required = false) String billDate,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		DailyBillConsumptionDetail dailyBillConsumptionDetail = new DailyBillConsumptionDetail();
		dailyBillConsumptionDetail.setUserId(id);
		dailyBillConsumptionDetail.setBillDate(billDate);
		dailyBillConsumptionDetail.setBillConsumptionTypeId(billConsumptionTypeId);
		int count = dailyBillConsumptionDetailService.count(dailyBillConsumptionDetail);
		if (count == 0) {
			return new ResponseRestEntity<List<DailyBillConsumptionDetail>>(new ArrayList<DailyBillConsumptionDetail>(), HttpRestStatus.NOT_FOUND);
		}
		List<DailyBillConsumptionDetail> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = dailyBillConsumptionDetailService.selectPage(dailyBillConsumptionDetail);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = dailyBillConsumptionDetailService.selectPage(dailyBillConsumptionDetail);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<DailyBillConsumptionDetail>>(new ArrayList<DailyBillConsumptionDetail>(),HttpRestStatus.NOT_FOUND);
		}
		
		List<DailyBillConsumptionDetail> listNew = new ArrayList<DailyBillConsumptionDetail>();
		for(DailyBillConsumptionDetail bean:list){
			ConsumptionType consumptionType = consumptionTypeService.selectByPrimaryKey(bean.getBillConsumptionTypeId());
			if(consumptionType!=null){
				bean.setConsumptionType(consumptionType.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<DailyBillConsumptionDetail>>(listNew, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query DailyBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<DailyBillConsumptionDetail> selectByPrimaryKey(@PathVariable("dailyBillId") String dailyBillId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId) {
		DailyBillConsumptionDetail bean = new DailyBillConsumptionDetail();
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		DailyBillConsumptionDetail dailyBillConsumptionDetail = dailyBillConsumptionDetailService.selectByPrimaryKey(bean);
		if (dailyBillConsumptionDetail == null) {
			return new ResponseRestEntity<DailyBillConsumptionDetail>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<DailyBillConsumptionDetail>(dailyBillConsumptionDetail, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add DailyBillConsumptionDetail", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createDailyBillConsumptionDetail(@Valid @RequestBody DailyBillConsumptionDetail dailyBillConsumptionDetail,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		dailyBillConsumptionDetailService.insert(dailyBillConsumptionDetail);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/dailyBillConsumptionDetail/{id}").buildAndExpand(dailyBillConsumptionDetail.getBillConsumptionTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit DailyBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{userId}/{billConsumptionTypeId}/{billDate}", method = RequestMethod.PUT)
	public ResponseRestEntity<DailyBillConsumptionDetail> updateDailyBillConsumptionDetail(@PathVariable("userId") String userId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,
			@PathVariable("billDate") String billDate,@Valid @RequestBody DailyBillConsumptionDetail dailyBillConsumptionDetail, BindingResult result) {
		DailyBillConsumptionDetail bean = new DailyBillConsumptionDetail();
		bean.setUserId(userId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		bean.setBillDate(billDate);
		DailyBillConsumptionDetail currentDailyBillConsumptionDetail = dailyBillConsumptionDetailService.selectByPrimaryKey(bean);

		if (currentDailyBillConsumptionDetail == null) {
			return new ResponseRestEntity<DailyBillConsumptionDetail>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentDailyBillConsumptionDetail.setBorrow(dailyBillConsumptionDetail.getBorrow());
		currentDailyBillConsumptionDetail.setLoan(dailyBillConsumptionDetail.getLoan());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<DailyBillConsumptionDetail>(currentDailyBillConsumptionDetail,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		dailyBillConsumptionDetailService.updateByPrimaryKey(currentDailyBillConsumptionDetail);

		return new ResponseRestEntity<DailyBillConsumptionDetail>(currentDailyBillConsumptionDetail, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part DailyBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{userId}/{billConsumptionTypeId}/{billDate}", method = RequestMethod.PATCH)
	public ResponseRestEntity<DailyBillConsumptionDetail> updateDailyBillConsumptionDetailSelective(@PathVariable("userId") String userId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,
			@PathVariable("billDate") String billDate,@Valid @RequestBody DailyBillConsumptionDetail dailyBillConsumptionDetail, BindingResult result) {
		DailyBillConsumptionDetail bean = new DailyBillConsumptionDetail();
		bean.setUserId(userId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		bean.setBillDate(billDate);
		DailyBillConsumptionDetail currentDailyBillConsumptionDetail = dailyBillConsumptionDetailService.selectByPrimaryKey(bean);

		if (currentDailyBillConsumptionDetail == null) {
			return new ResponseRestEntity<DailyBillConsumptionDetail>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentDailyBillConsumptionDetail.setBorrow(dailyBillConsumptionDetail.getBorrow());
		currentDailyBillConsumptionDetail.setLoan(dailyBillConsumptionDetail.getLoan());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<DailyBillConsumptionDetail>(currentDailyBillConsumptionDetail,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		dailyBillConsumptionDetailService.updateByPrimaryKey(currentDailyBillConsumptionDetail);

		return new ResponseRestEntity<DailyBillConsumptionDetail>(currentDailyBillConsumptionDetail, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Delete DailyBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{userId}/{billConsumptionTypeId}/{billDate}", method = RequestMethod.DELETE)
	public ResponseRestEntity<DailyBillConsumptionDetail> deleteDailyBillConsumptionDetail(@PathVariable("userId") String userId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,@PathVariable("billDate") String billDate) {
		DailyBillConsumptionDetail bean = new DailyBillConsumptionDetail();
		bean.setUserId(userId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		bean.setBillDate(billDate);
		DailyBillConsumptionDetail dailyBillConsumptionDetail = dailyBillConsumptionDetailService.selectByPrimaryKey(bean);
		if (dailyBillConsumptionDetail == null) {
			return new ResponseRestEntity<DailyBillConsumptionDetail>(HttpRestStatus.NOT_FOUND);
		}

		dailyBillConsumptionDetailService.deleteByPrimaryKey(bean);
		return new ResponseRestEntity<DailyBillConsumptionDetail>(HttpRestStatus.NO_CONTENT);
	}
}