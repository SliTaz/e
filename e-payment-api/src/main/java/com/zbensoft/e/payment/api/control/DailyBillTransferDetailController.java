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
import com.zbensoft.e.payment.api.service.api.DailyBillTransferDetailService;
import com.zbensoft.e.payment.api.service.api.TransferTypeService;
import com.zbensoft.e.payment.db.domain.DailyBillTransferDetail;
import com.zbensoft.e.payment.db.domain.MonthBillTransferDetail;
import com.zbensoft.e.payment.db.domain.TransferType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/dailyBillTransferDetail")
@RestController
public class DailyBillTransferDetailController {
	@Autowired
	DailyBillTransferDetailService dailyBillTransferDetailService;
	@Autowired
	TransferTypeService transferTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query DailyBillTransferDetail，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<DailyBillTransferDetail>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String transferTypeId,
			@RequestParam(required = false) String billDate,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		DailyBillTransferDetail dailyBillTransferDetail = new DailyBillTransferDetail();
		dailyBillTransferDetail.setUserId(id);
		dailyBillTransferDetail.setBillDate(billDate);
		dailyBillTransferDetail.setTransferTypeId(transferTypeId);
		int count = dailyBillTransferDetailService.count(dailyBillTransferDetail);
		if (count == 0) {
			return new ResponseRestEntity<List<DailyBillTransferDetail>>(new ArrayList<DailyBillTransferDetail>(), HttpRestStatus.NOT_FOUND);
		}
		List<DailyBillTransferDetail> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = dailyBillTransferDetailService.selectPage(dailyBillTransferDetail);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = dailyBillTransferDetailService.selectPage(dailyBillTransferDetail);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<DailyBillTransferDetail>>(new ArrayList<DailyBillTransferDetail>(),HttpRestStatus.NOT_FOUND);
		}
		List<DailyBillTransferDetail> listNew = new ArrayList<DailyBillTransferDetail>();
		for(DailyBillTransferDetail bean:list){
			TransferType transferType = transferTypeService.selectByPrimaryKey(bean.getTransferTypeId());
			if(transferType!=null){
				bean.setTransferType(transferType.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<DailyBillTransferDetail>>(listNew, HttpRestStatus.OK, count, count);
	}

/*	@ApiOperation(value = "Query DailyBillTransferDetail", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<DailyBillTransferDetail> selectByPrimaryKey(@PathVariable("dailyBillId") String dailyBillId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId) {
		DailyBillTransferDetail bean = new DailyBillTransferDetail();
		bean.setDailyBillId(dailyBillId);
		bean.setTransferTypeId(billConsumptionTypeId);
		DailyBillTransferDetail dailyBillTransferDetail = dailyBillTransferDetailService.selectByPrimaryKey(bean);
		if (dailyBillTransferDetail == null) {
			return new ResponseRestEntity<DailyBillTransferDetail>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<DailyBillTransferDetail>(dailyBillTransferDetail, HttpRestStatus.OK);
	}
*/
	@ApiOperation(value = "Add DailyBillTransferDetail", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createDailyBillTransferDetail(@Valid @RequestBody DailyBillTransferDetail dailyBillTransferDetail,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		dailyBillTransferDetailService.insert(dailyBillTransferDetail);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/dailyBillTransferDetail/{id}").buildAndExpand(dailyBillTransferDetail.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit DailyBillTransferDetail", notes = "")
	@RequestMapping(value = "/{userId}/{transferTypeId}/{billDate}", method = RequestMethod.PUT)
	public ResponseRestEntity<DailyBillTransferDetail> updateDailyBillTransferDetail(@PathVariable("userId") String userId,@PathVariable("transferTypeId") String transferTypeId,
			@PathVariable("billDate") String billDate,@Valid @RequestBody DailyBillTransferDetail dailyBillTransferDetail, BindingResult result) {
		DailyBillTransferDetail bean = new DailyBillTransferDetail();
		bean.setUserId(userId);
		bean.setTransferTypeId(transferTypeId);
		bean.setBillDate(billDate);
		DailyBillTransferDetail currentDailyBillTransferDetail = dailyBillTransferDetailService.selectByPrimaryKey(bean);

		if (currentDailyBillTransferDetail == null) {
			return new ResponseRestEntity<DailyBillTransferDetail>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}


		currentDailyBillTransferDetail.setBorrow(dailyBillTransferDetail.getBorrow());
		currentDailyBillTransferDetail.setLoan(dailyBillTransferDetail.getLoan());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<DailyBillTransferDetail>(currentDailyBillTransferDetail,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		dailyBillTransferDetailService.updateByPrimaryKey(currentDailyBillTransferDetail);

		return new ResponseRestEntity<DailyBillTransferDetail>(currentDailyBillTransferDetail, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

/*	@ApiOperation(value = "Edit Part DailyBillTransferDetail", notes = "")
	@RequestMapping(value = "/{dailyBillId}/{billConsumptionTypeId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<DailyBillTransferDetail> updateDailyBillTransferDetailSelective(@PathVariable("dailyBillId") String dailyBillId,
			@PathVariable("billConsumptionTypeId") String billConsumptionTypeId,
			@RequestBody DailyBillTransferDetail dailyBillTransferDetail) {

		DailyBillTransferDetail bean = new DailyBillTransferDetail();
		bean.setDailyBillId(dailyBillId);
		bean.setTransferTypeId(billConsumptionTypeId);
		DailyBillTransferDetail currentDailyBillTransferDetail = dailyBillTransferDetailService.selectByPrimaryKey(bean);

		if (currentDailyBillTransferDetail == null) {
			return new ResponseRestEntity<DailyBillTransferDetail>(HttpRestStatus.NOT_FOUND);
		}
		currentDailyBillTransferDetail.setDailyBillId(dailyBillTransferDetail.getDailyBillId());
		currentDailyBillTransferDetail.setTransferTypeId(dailyBillTransferDetail.getTransferTypeId());
		currentDailyBillTransferDetail.setBorrow(dailyBillTransferDetail.getBorrow());
		currentDailyBillTransferDetail.setLoan(dailyBillTransferDetail.getLoan());
		dailyBillTransferDetailService.updateByPrimaryKeySelective(dailyBillTransferDetail);

		return new ResponseRestEntity<DailyBillTransferDetail>(currentDailyBillTransferDetail, HttpRestStatus.OK);
	}
*/
	@ApiOperation(value = "Delete DailyBillTransferDetail", notes = "")
	@RequestMapping(value = "/{userId}/{transferTypeId}/{billDate}", method = RequestMethod.DELETE)
	public ResponseRestEntity<DailyBillTransferDetail> deleteDailyBillTransferDetail(@PathVariable("userId") String userId,@PathVariable("transferTypeId") String transferTypeId,
			@PathVariable("billDate") String billDate) {
		DailyBillTransferDetail bean = new DailyBillTransferDetail();
		bean.setUserId(userId);
		bean.setTransferTypeId(transferTypeId);
		bean.setBillDate(billDate);
		DailyBillTransferDetail dailyBillTransferDetail = dailyBillTransferDetailService.selectByPrimaryKey(bean);
		if (dailyBillTransferDetail == null) {
			return new ResponseRestEntity<DailyBillTransferDetail>(HttpRestStatus.NOT_FOUND);
		}

		dailyBillTransferDetailService.deleteByPrimaryKey(bean);
		return new ResponseRestEntity<DailyBillTransferDetail>(HttpRestStatus.NO_CONTENT);
	}

}