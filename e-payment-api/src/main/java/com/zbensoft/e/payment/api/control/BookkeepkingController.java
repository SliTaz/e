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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.BookkeepkingService;
import com.zbensoft.e.payment.api.service.api.CaptionAccountService;
import com.zbensoft.e.payment.db.domain.Bookkeepking;
import com.zbensoft.e.payment.db.domain.CaptionAccount;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/bookkeepking")
@RestController
public class BookkeepkingController {
	@Autowired
	BookkeepkingService bookkeepkingService;
	@Autowired
	CaptionAccountService captionAccountService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询会计记账，支持分页
	@PreAuthorize("hasRole('R_ACCOUNT_B_Q')")
	@ApiOperation(value = "Query Bookkeepking，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<Bookkeepking>> selectPage(@RequestParam(required = false) String bookkeepking_seq,
			@RequestParam(required = false) String tradeSeq,
			@RequestParam(required = false) String borrowCaption,
			@RequestParam(required = false) String loanCaption,
			@RequestParam(required = false) String createTimeEnd,
			 @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		Bookkeepking bookkeepking = new Bookkeepking();
		// 必须输入一个进行查询
		if ((bookkeepking_seq == null || "".equals(bookkeepking_seq)) && (tradeSeq == null || "".equals(tradeSeq)) && (borrowCaption == null || "".equals(borrowCaption))
				&& (loanCaption == null || "".equals(loanCaption))&& (createTimeEnd == null || "".equals(createTimeEnd))) {
			return new ResponseRestEntity<List<Bookkeepking>>(new ArrayList<Bookkeepking>(), HttpRestStatus.NOT_FOUND);
		}
		bookkeepking.setBookkeepkingSeq(bookkeepking_seq);
		bookkeepking.setTradeSeq(tradeSeq);
		bookkeepking.setDeleteFlag(0);
		bookkeepking.setBorrowCaption(borrowCaption);
		bookkeepking.setLoanCaption(loanCaption);
		//System.out.println("执行了后台的分页查询:bookkeepking_seq="+bookkeepking_seq+";caption_account_code="+caption_account_code);
		bookkeepking.setCreateTimeEnd(createTimeEnd);
		List<Bookkeepking> list = new ArrayList<Bookkeepking>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = bookkeepkingService.selectPage(bookkeepking);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = bookkeepkingService.selectPage(bookkeepking);
		}

		int count = bookkeepkingService.count(bookkeepking);
		// 分页 end

		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<Bookkeepking>>(new ArrayList<Bookkeepking>(), HttpRestStatus.NOT_FOUND);
		}
		List<Bookkeepking> listNew = new ArrayList<Bookkeepking>();
		for(Bookkeepking bean:list){
			CaptionAccount captionAccount1 = captionAccountService.selectByPrimaryKey(bean.getBorrowCaption());
			CaptionAccount captionAccount2 = captionAccountService.selectByPrimaryKey(bean.getLoanCaption());
			if(captionAccount1!=null){
				bean.setBorrowCaptionName(captionAccount1.getName());
			}
			if(captionAccount2!=null){
				bean.setLoanCaptionName(captionAccount2.getName());
			}
			listNew.add(bean);
			}
		return new ResponseRestEntity<List<Bookkeepking>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询会计记账
	@PreAuthorize("hasRole('R_ACCOUNT_B_Q')")
	@ApiOperation(value = "Query Bookkeepking", notes = "")
	@RequestMapping(value = "/{bookkeepking_seq}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<Bookkeepking> selectByPrimaryKey(
			@PathVariable("bookkeepking_seq") String bookkeepking_seq) {
		Bookkeepking bookkeepking = bookkeepkingService.selectByPrimaryKey(bookkeepking_seq);
		if (bookkeepking == null) {
			return new ResponseRestEntity<Bookkeepking>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<Bookkeepking>(bookkeepking, HttpRestStatus.OK);
	}

	// 新增会计记账
	@PreAuthorize("hasRole('R_ACCOUNT_B_E')")
	@ApiOperation(value = "Add Bookkeepking", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody Bookkeepking bookkeepking, BindingResult result,
			UriComponentsBuilder ucBuilder) {

		bookkeepking.setBookkeepkingSeq(IDGenerate.generateCommTwo(IDGenerate.BOOKKEEPKING));
		if (bookkeepkingService.isExist(bookkeepking)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("bookkeepking.create.conflict.message"));
		}

		bookkeepking.setCreateTime(PageHelperUtil.getCurrentDate());
		bookkeepking.setDeleteFlag(PageHelperUtil.DELETE_NO);

		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		bookkeepkingService.insert(bookkeepking);
		//新增日志
     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, bookkeepking,CommonLogImpl.ACCOUNTING);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/bookkeepking/{{bookkeepking_seq}}")
				.buildAndExpand(bookkeepking.getBookkeepkingSeq()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("bookkeepking.create.created.message"));
	}

	// 修改会计记账信息
	@PreAuthorize("hasRole('R_ACCOUNT_B_E')")
	@ApiOperation(value = "Edit Bookkeepking", notes = "")
	@RequestMapping(value = "{bookkeepking_seq}", method = RequestMethod.PUT)
	public ResponseRestEntity<Bookkeepking> update(@PathVariable("bookkeepking_seq") String bookkeepking_seq,
			@Valid @RequestBody Bookkeepking bookkeepking, BindingResult result) {

		Bookkeepking currentBookkeepking = bookkeepkingService.selectByPrimaryKey(bookkeepking_seq);

		if (currentBookkeepking == null) {
			return new ResponseRestEntity<Bookkeepking>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("bookkeepking.update.not_found.message"));
		}

		currentBookkeepking.setTradeSeq(bookkeepking.getTradeSeq());
		currentBookkeepking.setBorrowCaption(bookkeepking.getBorrowCaption());
		currentBookkeepking.setBorrowAmount(bookkeepking.getBorrowAmount());
		currentBookkeepking.setLoanCaption(bookkeepking.getLoanCaption());
		currentBookkeepking.setLoanAmount(bookkeepking.getLoanAmount());
		currentBookkeepking.setRemark(bookkeepking.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<Bookkeepking>(currentBookkeepking,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		bookkeepkingService.updateByPrimaryKey(currentBookkeepking);
		//修改日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBookkeepking,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<Bookkeepking>(currentBookkeepking, HttpRestStatus.OK,localeMessageSourceService.getMessage("bookkeepking.update.ok.message"));
	}

	// 修改部分会计记账信息
	@PreAuthorize("hasRole('R_ACCOUNT_B_E')")
	@ApiOperation(value = "Edit Part Bookkeepking", notes = "")
	@RequestMapping(value = "{bookkeepking_seq}", method = RequestMethod.PATCH)
	public ResponseRestEntity<Bookkeepking> updateSelective(@PathVariable("bookkeepking_seq") String bookkeepking_seq,
			@RequestBody Bookkeepking bookkeepking) {

		Bookkeepking currentBookkeepking = bookkeepkingService.selectByPrimaryKey(bookkeepking_seq);

		if (currentBookkeepking == null) {
			return new ResponseRestEntity<Bookkeepking>(HttpRestStatus.NOT_FOUND);
		}
		bookkeepking.setBookkeepkingSeq(bookkeepking_seq);// ?
		bookkeepkingService.updateByPrimaryKeySelective(bookkeepking);
		//修改日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, bookkeepking,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<Bookkeepking>(currentBookkeepking, HttpRestStatus.OK);
	}

	// 删除指定会计记账
	@PreAuthorize("hasRole('R_ACCOUNT_B_E')")
	@ApiOperation(value = "Delete Bookkeepking", notes = "")
	@RequestMapping(value = "/{bookkeepking_seq}", method = RequestMethod.DELETE)
	public ResponseRestEntity<Bookkeepking> delete(@PathVariable("bookkeepking_seq") String bookkeepking_seq) {

		Bookkeepking bookkeepking = bookkeepkingService.selectByPrimaryKey(bookkeepking_seq);
		if (bookkeepking == null) {
			return new ResponseRestEntity<Bookkeepking>(HttpRestStatus.NOT_FOUND);
		}

		bookkeepking.setDeleteFlag(1);
		bookkeepkingService.updateByPrimaryKeySelective(bookkeepking);
		//删除日志开始
		Bookkeepking delBean = new Bookkeepking();
		delBean.setBookkeepkingSeq(bookkeepking_seq);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ACCOUNTING);
		//删除日志结束
		return new ResponseRestEntity<Bookkeepking>(HttpRestStatus.NO_CONTENT);
	}
}