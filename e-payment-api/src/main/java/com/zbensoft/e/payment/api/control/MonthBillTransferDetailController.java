package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MonthBillTransferDetailService;
import com.zbensoft.e.payment.api.service.api.TransferTypeService;
import com.zbensoft.e.payment.db.domain.ConsumptionType;
import com.zbensoft.e.payment.db.domain.MonthBillConsumptionDetail;
import com.zbensoft.e.payment.db.domain.MonthBillTransferDetail;
import com.zbensoft.e.payment.db.domain.TransferType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/monthBillTransferDetail")
@RestController
public class MonthBillTransferDetailController {
	@Autowired
	MonthBillTransferDetailService monthBillTransferDetailService;
	@Autowired
	TransferTypeService transferTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_TRADE_M_B_Q')")
	@ApiOperation(value = "Query MonthBillTransferDetail，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MonthBillTransferDetail>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String transferTypeId,
			@RequestParam(required = false) String billDate,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MonthBillTransferDetail monthBillTransferDetail = new MonthBillTransferDetail();
		monthBillTransferDetail.setUserId(id);
		monthBillTransferDetail.setTransferTypeId(transferTypeId);
		monthBillTransferDetail.setBillDate(billDate);
		int count = monthBillTransferDetailService.count(monthBillTransferDetail);
		if (count == 0) {
			return new ResponseRestEntity<List<MonthBillTransferDetail>>(new ArrayList<MonthBillTransferDetail>(), HttpRestStatus.NOT_FOUND);
		}
		List<MonthBillTransferDetail> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = monthBillTransferDetailService.selectPage(monthBillTransferDetail);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = monthBillTransferDetailService.selectPage(monthBillTransferDetail);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MonthBillTransferDetail>>(new ArrayList<MonthBillTransferDetail>(),HttpRestStatus.NOT_FOUND);
		}
		List<MonthBillTransferDetail> listNew = new ArrayList<MonthBillTransferDetail>();
		for(MonthBillTransferDetail bean:list){
			TransferType transferType = transferTypeService.selectByPrimaryKey(bean.getTransferTypeId());
			if(transferType!=null){
				bean.setTransferType(transferType.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<MonthBillTransferDetail>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_Q')")
	@ApiOperation(value = "Query MonthBillTransferDetail", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MonthBillTransferDetail> selectByPrimaryKey(@PathVariable("monthBillId") String monthBillId,
			@PathVariable("transferTypeId") String transferTypeId) {
		MonthBillTransferDetail bean = new MonthBillTransferDetail();
		bean.setTransferTypeId(transferTypeId);
		MonthBillTransferDetail monthBillTransferDetail = monthBillTransferDetailService.selectByPrimaryKey(bean);
		if (monthBillTransferDetail == null) {
			return new ResponseRestEntity<MonthBillTransferDetail>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MonthBillTransferDetail>(monthBillTransferDetail, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Add MonthBillTransferDetail", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMonthBillTransferDetail(@Valid @RequestBody MonthBillTransferDetail monthBillTransferDetail,BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		monthBillTransferDetailService.insert(monthBillTransferDetail);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/monthBillTransferDetail/{id}").buildAndExpand(monthBillTransferDetail.getTransferTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Edit MonthBillTransferDetail", notes = "")
	@RequestMapping(value = "/{userId}/{transferTypeId}/{billDate}", method = RequestMethod.PUT)
	public ResponseRestEntity<MonthBillTransferDetail> updateMonthBillTransferDetail(@PathVariable("userId") String userId,@PathVariable("transferTypeId") String transferTypeId,
			@PathVariable("billDate") String billDate,@Valid @RequestBody MonthBillTransferDetail monthBillTransferDetail, BindingResult result) {
		MonthBillTransferDetail bean = new MonthBillTransferDetail();
		bean.setUserId(userId);
		bean.setTransferTypeId(transferTypeId);
		bean.setBillDate(billDate);
		MonthBillTransferDetail currentMonthBillTransferDetail = monthBillTransferDetailService.selectByPrimaryKey(bean);

		if (currentMonthBillTransferDetail == null) {
			return new ResponseRestEntity<MonthBillTransferDetail>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMonthBillTransferDetail.setTransferTypeId(monthBillTransferDetail.getTransferTypeId());
		currentMonthBillTransferDetail.setBorrow(monthBillTransferDetail.getBorrow());
		currentMonthBillTransferDetail.setLoan(monthBillTransferDetail.getLoan());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MonthBillTransferDetail>(currentMonthBillTransferDetail,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		monthBillTransferDetailService.updateByPrimaryKey(currentMonthBillTransferDetail);

		return new ResponseRestEntity<MonthBillTransferDetail>(currentMonthBillTransferDetail, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Edit Part MonthBillTransferDetail", notes = "")
	@RequestMapping(value = "/{userId}/{transferTypeId}/{billDate}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MonthBillTransferDetail> updateMonthBillTransferDetailSelective(@PathVariable("userId") String userId,
			@PathVariable("billDate") String billDate,
			@PathVariable("transferTypeId") String transferTypeId,
			@RequestBody MonthBillTransferDetail monthBillTransferDetail,BindingResult result) {

		MonthBillTransferDetail bean = new MonthBillTransferDetail();
		bean.setUserId(userId);
		bean.setTransferTypeId(transferTypeId);
		bean.setBillDate(billDate);
		MonthBillTransferDetail currentMonthBillTransferDetail = monthBillTransferDetailService.selectByPrimaryKey(bean);

		if (currentMonthBillTransferDetail == null) {
			return new ResponseRestEntity<MonthBillTransferDetail>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentMonthBillTransferDetail.setBorrow(monthBillTransferDetail.getBorrow());
		currentMonthBillTransferDetail.setLoan(monthBillTransferDetail.getLoan());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MonthBillTransferDetail>(currentMonthBillTransferDetail,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		monthBillTransferDetailService.updateByPrimaryKey(currentMonthBillTransferDetail);

		return new ResponseRestEntity<MonthBillTransferDetail>(currentMonthBillTransferDetail, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_TRADE_M_B_E')")
	@ApiOperation(value = "Delete MonthBillTransferDetail", notes = "")
	@RequestMapping(value = "/{userId}/{transferTypeId}/{billDate}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MonthBillTransferDetail> deleteMonthBillTransferDetail(@PathVariable("userId") String userId,
			@PathVariable("transferTypeId") String transferTypeId,@PathVariable("billDate") String billDate) {
		MonthBillTransferDetail bean = new MonthBillTransferDetail();
		bean.setUserId(userId);
		bean.setBillDate(billDate);
		bean.setTransferTypeId(transferTypeId);
		MonthBillTransferDetail monthBillTransferDetail = monthBillTransferDetailService.selectByPrimaryKey(bean);
		if (monthBillTransferDetail == null) {
			return new ResponseRestEntity<MonthBillTransferDetail>(HttpRestStatus.NOT_FOUND);
		}

		monthBillTransferDetailService.deleteByPrimaryKey(bean);
		return new ResponseRestEntity<MonthBillTransferDetail>(HttpRestStatus.NO_CONTENT);
	}
	

	@PreAuthorize("hasRole('R_TRADE_M_B_Q') or hasRole('CONSUMER')")
	@RequestMapping(value = "/queryMontyBillTransferDetail", method = RequestMethod.GET)
	public ResponseRestEntity<List<MonthBillTransferDetail>> queryMontyBillTransferDetail(@RequestParam(required=true) String userId,@RequestParam(required=true) String monthArr) {
		List<MonthBillTransferDetail> monthBillTransferDetailList=new ArrayList<MonthBillTransferDetail>();
		if("".equals(monthArr)||null==monthArr)
		{
			return new ResponseRestEntity<List<MonthBillTransferDetail>>(HttpRestStatus.NO_CONTENT);
		}
		
		monthBillTransferDetailList =  monthBillTransferDetailService.queryMontyBillTransferDetail(userId,monthArr.split(","));
		return new ResponseRestEntity<List<MonthBillTransferDetail>>(monthBillTransferDetailList,HttpRestStatus.OK);
	}
}