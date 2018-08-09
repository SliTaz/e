package com.zbensoft.e.payment.api.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ReconciliationBatchService;
import com.zbensoft.e.payment.db.domain.ReconciliationBatch;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/reconciliationMerchant")
@RestController
public class ReconciliationMerchantController {
	@Autowired
	ReconciliationBatchService reconciliationBatchService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query ReconciliationBatch，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ReconciliationBatch>> selectPage(@RequestParam(required = false) String id,
			
			@RequestParam(required = false) String reconciliationTimeStart,@RequestParam(required = false) String reconciliationTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ReconciliationBatch reconciliationBatch = new ReconciliationBatch();
		reconciliationBatch.setReconciliationBatchId(id);
		
		reconciliationBatch.setType(MessageDef.RECONCILIATION_TYPE.MERCHANT);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = sdf.format(cal.getTime());
		if (reconciliationTimeStart == null || "".equals(reconciliationTimeStart)) {
			reconciliationBatch.setReconciliationTimeStart(yesterday);
		} else {
			reconciliationBatch.setReconciliationTimeStart(reconciliationTimeStart);
		}
		List<ReconciliationBatch> list = new ArrayList<ReconciliationBatch>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = reconciliationBatchService.selectPage(reconciliationBatch);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = reconciliationBatchService.selectPage(reconciliationBatch);
		}

		int count = reconciliationBatchService.count(reconciliationBatch);
		// 分页 end
		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ReconciliationBatch>>(new ArrayList<ReconciliationBatch>(),HttpRestStatus.NOT_FOUND);
		}
	
		return new ResponseRestEntity<List<ReconciliationBatch>>(list, HttpRestStatus.OK,count,count);
	
	}

	@ApiOperation(value = "Query ReconciliationBatch", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ReconciliationBatch> selectByPrimaryKey(@PathVariable("id") String id) {
		ReconciliationBatch reconciliationBatch = reconciliationBatchService.selectByPrimaryKey(id);
		
		if (reconciliationBatch == null) {
			return new ResponseRestEntity<ReconciliationBatch>(HttpRestStatus.NOT_FOUND);
		}
		reconciliationBatch.setType(2);
		return new ResponseRestEntity<ReconciliationBatch>(reconciliationBatch, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add ReconciliationBatch", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createBatch(@RequestBody ReconciliationBatch reconciliationBatch,
			UriComponentsBuilder ucBuilder) {
		
		reconciliationBatch.setReconciliationBatchId(IDGenerate.generateCommTwo(IDGenerate.RECONCILIATION_BATCH));

		reconciliationBatchService.insert(reconciliationBatch);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/reconciliationBatch/{id}")
				.buildAndExpand(reconciliationBatch.getReconciliationBatchId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit ReconciliationBatch", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ReconciliationBatch> updateBatch(@PathVariable("id") String id,
			@RequestBody ReconciliationBatch reconciliationBatch) {

		ReconciliationBatch currentBatch = reconciliationBatchService.selectByPrimaryKey(id);

		if (currentBatch == null) {
			return new ResponseRestEntity<ReconciliationBatch>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentBatch.setType(2);
		currentBatch.setReconciliationTime(reconciliationBatch.getReconciliationTime());
		currentBatch.setProgress(reconciliationBatch.getProgress());
		currentBatch.setSuccCount(reconciliationBatch.getSuccCount());
		currentBatch.setSuccAmount(reconciliationBatch.getSuccAmount());
		currentBatch.setErrorCount(reconciliationBatch.getErrorCount());
		currentBatch.setErrorAmount(reconciliationBatch.getErrorAmount());
		currentBatch.setUnhandlingErrorCount(reconciliationBatch.getUnhandlingErrorCount());
		currentBatch.setTradeCount(reconciliationBatch.getTradeCount());
		currentBatch.setForeignTradeCount(reconciliationBatch.getForeignTradeCount());
		currentBatch.setTradeAmount(reconciliationBatch.getTradeAmount());
		currentBatch.setForeignTradeAmount(reconciliationBatch.getForeignTradeAmount());
		currentBatch.setRefoundCount(reconciliationBatch.getRefoundCount());
		currentBatch.setRefoundAmount(reconciliationBatch.getRefoundAmount());
		currentBatch.setForeignRefoundAmount(reconciliationBatch.getForeignRefoundAmount());
		currentBatch.setFeeAmount(reconciliationBatch.getFeeAmount());
		currentBatch.setForeignFeeAmount(reconciliationBatch.getForeignRefoundAmount());
		currentBatch.setBorrowAmount(reconciliationBatch.getBorrowAmount());
		currentBatch.setLoanAmount(reconciliationBatch.getLoanAmount());
		currentBatch.setCreateTime(reconciliationBatch.getCreateTime());
		reconciliationBatchService.updateByPrimaryKey(currentBatch);
		//修改日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBatch,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ReconciliationBatch>(currentBatch, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part ReconciliationBatch", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ReconciliationBatch> updateBatchSelective(@PathVariable("id") String id,
			@RequestBody ReconciliationBatch reconciliationBatch) {

		ReconciliationBatch currentBatch = reconciliationBatchService.selectByPrimaryKey(id);

		if (currentBatch == null) {
			return new ResponseRestEntity<ReconciliationBatch>(HttpRestStatus.NOT_FOUND);
		}
		reconciliationBatch.setReconciliationBatchId(id);
		currentBatch.setType(reconciliationBatch.getType());
		currentBatch.setReconciliationTime(reconciliationBatch.getReconciliationTime());
		currentBatch.setProgress(reconciliationBatch.getProgress());
		currentBatch.setSuccCount(reconciliationBatch.getSuccCount());
		currentBatch.setSuccAmount(reconciliationBatch.getSuccAmount());
		currentBatch.setErrorCount(reconciliationBatch.getErrorCount());
		currentBatch.setErrorAmount(reconciliationBatch.getErrorAmount());
		currentBatch.setUnhandlingErrorCount(reconciliationBatch.getUnhandlingErrorCount());
		currentBatch.setTradeCount(reconciliationBatch.getTradeCount());
		currentBatch.setForeignTradeCount(reconciliationBatch.getForeignTradeCount());
		currentBatch.setTradeAmount(reconciliationBatch.getTradeAmount());
		currentBatch.setForeignTradeAmount(reconciliationBatch.getForeignTradeAmount());
		currentBatch.setRefoundCount(reconciliationBatch.getRefoundCount());
		currentBatch.setRefoundAmount(reconciliationBatch.getRefoundAmount());
		currentBatch.setForeignRefoundAmount(reconciliationBatch.getForeignRefoundAmount());
		currentBatch.setFeeAmount(reconciliationBatch.getFeeAmount());
		currentBatch.setForeignFeeAmount(reconciliationBatch.getForeignRefoundAmount());
		currentBatch.setBorrowAmount(reconciliationBatch.getBorrowAmount());
		currentBatch.setLoanAmount(reconciliationBatch.getLoanAmount());
		currentBatch.setCreateTime(reconciliationBatch.getCreateTime());
		reconciliationBatchService.updateByPrimaryKeySelective(reconciliationBatch);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, reconciliationBatch,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ReconciliationBatch>(currentBatch, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete ReconciliationBatch", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ReconciliationBatch> deleteBatch(@PathVariable("id") String id) {

		ReconciliationBatch reconciliationBatch = reconciliationBatchService.selectByPrimaryKey(id);
		if (reconciliationBatch == null) {
			return new ResponseRestEntity<ReconciliationBatch>(HttpRestStatus.NOT_FOUND);
		}

		reconciliationBatchService.deleteByPrimaryKey(id);
		//删除日志开始
		ReconciliationBatch delBean = new ReconciliationBatch();
		delBean.setReconciliationBatchId(id);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.RECONCILIATION);
		//删除日志结束
		return new ResponseRestEntity<ReconciliationBatch>(HttpRestStatus.NO_CONTENT);
	}

}