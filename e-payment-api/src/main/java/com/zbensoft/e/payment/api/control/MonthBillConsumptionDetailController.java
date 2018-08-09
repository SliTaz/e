package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumptionTypeService;
import com.zbensoft.e.payment.api.service.api.MonthBillConsumptionDetailService;
import com.zbensoft.e.payment.db.domain.ConsumptionType;
import com.zbensoft.e.payment.db.domain.MonthBillConsumptionDetail;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/monthBillConsumptionDetail")
@RestController
public class MonthBillConsumptionDetailController {
	@Autowired
	MonthBillConsumptionDetailService monthBillConsumptionDetailService;
	@Autowired
	ConsumptionTypeService consumptionTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_TRADE_M_B_Q')")
	@ApiOperation(value = "Query MonthBillConsumptionDetail，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MonthBillConsumptionDetail>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String billConsumptionTypeId,
			@RequestParam(required = false) String billDate,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MonthBillConsumptionDetail monthBillConsumptionDetail = new MonthBillConsumptionDetail();
		monthBillConsumptionDetail.setUserId(id);
		monthBillConsumptionDetail.setBillDate(billDate);
		monthBillConsumptionDetail.setBillConsumptionTypeId(billConsumptionTypeId);
		int count = monthBillConsumptionDetailService.count(monthBillConsumptionDetail);
		if (count == 0) {
			return new ResponseRestEntity<List<MonthBillConsumptionDetail>>(new ArrayList<MonthBillConsumptionDetail>(), HttpRestStatus.NOT_FOUND);
		}
		List<MonthBillConsumptionDetail> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = monthBillConsumptionDetailService.selectPage(monthBillConsumptionDetail);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = monthBillConsumptionDetailService.selectPage(monthBillConsumptionDetail);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MonthBillConsumptionDetail>>(new ArrayList<MonthBillConsumptionDetail>(),HttpRestStatus.NOT_FOUND);
		}
		List<MonthBillConsumptionDetail> listNew = new ArrayList<MonthBillConsumptionDetail>();
		for(MonthBillConsumptionDetail bean:list){
			ConsumptionType consumptionType = consumptionTypeService.selectByPrimaryKey(bean.getBillConsumptionTypeId());
			if(consumptionType!=null){
				bean.setConsumptionType(consumptionType.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<MonthBillConsumptionDetail>>(listNew, HttpRestStatus.OK, count, count);
	}

/*	@ApiOperation(value = "Query MonthBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MonthBillConsumptionDetail> selectByPrimaryKey(@PathVariable("monthBillId") String monthBillId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId) {
		MonthBillConsumptionDetail bean = new MonthBillConsumptionDetail();
		bean.setMonthBillId(monthBillId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		MonthBillConsumptionDetail monthBillConsumptionDetail = monthBillConsumptionDetailService.selectByPrimaryKey(bean);
		if (monthBillConsumptionDetail == null) {
			return new ResponseRestEntity<MonthBillConsumptionDetail>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MonthBillConsumptionDetail>(monthBillConsumptionDetail, HttpRestStatus.OK);
	}*/

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Add MonthBillConsumptionDetail", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMonthBillConsumptionDetail(@Valid @RequestBody MonthBillConsumptionDetail monthBillConsumptionDetail,BindingResult result, UriComponentsBuilder ucBuilder) {

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		monthBillConsumptionDetailService.insert(monthBillConsumptionDetail);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/monthBillConsumptionDetail/{id}").buildAndExpand(monthBillConsumptionDetail.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Edit MonthBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{userId}/{billConsumptionTypeId}/{billDate}", method = RequestMethod.PUT)
	public ResponseRestEntity<MonthBillConsumptionDetail> updateMonthBillConsumptionDetail(@PathVariable("userId") String userId,@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,
			@PathVariable("billDate") String billDate,@Valid @RequestBody MonthBillConsumptionDetail monthBillConsumptionDetail, BindingResult result) {
		MonthBillConsumptionDetail bean = new MonthBillConsumptionDetail();
		bean.setUserId(userId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		bean.setBillDate(billDate);
		MonthBillConsumptionDetail currentMonthBillConsumptionDetail = monthBillConsumptionDetailService.selectByPrimaryKey(bean);

		if (currentMonthBillConsumptionDetail == null) {
			return new ResponseRestEntity<MonthBillConsumptionDetail>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMonthBillConsumptionDetail.setBorrow(monthBillConsumptionDetail.getBorrow());
		currentMonthBillConsumptionDetail.setLoan(monthBillConsumptionDetail.getLoan());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MonthBillConsumptionDetail>(currentMonthBillConsumptionDetail,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		monthBillConsumptionDetailService.updateByPrimaryKey(currentMonthBillConsumptionDetail);

		return new ResponseRestEntity<MonthBillConsumptionDetail>(currentMonthBillConsumptionDetail, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

/*	@ApiOperation(value = "Edit Part MonthBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{monthBillId}/{billConsumptionTypeId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MonthBillConsumptionDetail> updateMonthBillConsumptionDetailSelective(@PathVariable("monthBillId") String monthBillId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,
			@RequestBody MonthBillConsumptionDetail monthBillConsumptionDetail) {

		MonthBillConsumptionDetail bean = new MonthBillConsumptionDetail();
		bean.setMonthBillId(monthBillId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		MonthBillConsumptionDetail currentMonthBillConsumptionDetail = monthBillConsumptionDetailService.selectByPrimaryKey(bean);

		if (currentMonthBillConsumptionDetail == null) {
			return new ResponseRestEntity<MonthBillConsumptionDetail>(HttpRestStatus.NOT_FOUND);
		}
		currentMonthBillConsumptionDetail.setMonthBillId(monthBillConsumptionDetail.getMonthBillId());
		currentMonthBillConsumptionDetail.setBillConsumptionTypeId(monthBillConsumptionDetail.getBillConsumptionTypeId());
		currentMonthBillConsumptionDetail.setBorrow(monthBillConsumptionDetail.getBorrow());
		currentMonthBillConsumptionDetail.setLoan(monthBillConsumptionDetail.getLoan());
		monthBillConsumptionDetailService.updateByPrimaryKeySelective(monthBillConsumptionDetail);

		return new ResponseRestEntity<MonthBillConsumptionDetail>(currentMonthBillConsumptionDetail, HttpRestStatus.OK);
	}*/

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Delete MonthBillConsumptionDetail", notes = "")
	@RequestMapping(value = "/{userId}/{billConsumptionTypeId}/{billDate}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MonthBillConsumptionDetail> deleteMonthBillConsumptionDetail(@PathVariable("userId") String userId,@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,
			@PathVariable("billDate") String billDate) {
		MonthBillConsumptionDetail bean = new MonthBillConsumptionDetail();
		bean.setUserId(userId);
		bean.setBillConsumptionTypeId(billConsumptionTypeId);
		bean.setBillDate(billDate);
		MonthBillConsumptionDetail monthBillConsumptionDetail = monthBillConsumptionDetailService.selectByPrimaryKey(bean);
		if (monthBillConsumptionDetail == null) {
			return new ResponseRestEntity<MonthBillConsumptionDetail>(HttpRestStatus.NOT_FOUND);
		}

		monthBillConsumptionDetailService.deleteByPrimaryKey(bean);
		return new ResponseRestEntity<MonthBillConsumptionDetail>(HttpRestStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query Last Five MonthBill", notes = "")
	@RequestMapping(value = "/getLastFiveMonthBillDetail", method = RequestMethod.GET)
	public ResponseRestEntity<List<MonthBillConsumptionDetail>> queryMonthDetail(@RequestParam(required = true) String userId,@RequestParam(required = true) String month) {

		List<MonthBillConsumptionDetail> monthBillConsumptionDetailList=new ArrayList<MonthBillConsumptionDetail>();
		List<MonthBillConsumptionDetail> monthBillConsumptionDetailListNew=new ArrayList<MonthBillConsumptionDetail>();
		monthBillConsumptionDetailList=monthBillConsumptionDetailService.queryMonthDetailDetails(userId,month);
		
		if(monthBillConsumptionDetailList!=null&&monthBillConsumptionDetailList.size()>0){
			for(MonthBillConsumptionDetail bean :monthBillConsumptionDetailList){
				ConsumptionType consumptionType = consumptionTypeService.selectByPrimaryKey(bean.getBillConsumptionTypeId());
				if(consumptionType!=null){
					bean.setConsumptionType(consumptionType.getName());
				}
				monthBillConsumptionDetailListNew.add(bean);
			}
			}
	
		return new ResponseRestEntity<List<MonthBillConsumptionDetail>>(monthBillConsumptionDetailListNew,HttpRestStatus.OK);
	}

}