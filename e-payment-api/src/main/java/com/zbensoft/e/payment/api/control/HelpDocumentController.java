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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.CaptionAccountService;
import com.zbensoft.e.payment.api.service.api.HelpDocumentService;
import com.zbensoft.e.payment.db.domain.HelpDocument;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/helpDocument")
@RestController
public class HelpDocumentController {
	@Autowired
	HelpDocumentService helpDocumentService;
	@Autowired
	CaptionAccountService captionAccountService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询会计记账，支持分页
	@ApiOperation(value = "Query HelpDocument，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<HelpDocument>> selectPage(@RequestParam(required = false) String documentId,
			@RequestParam(required = false) String tradeSeq,
			@RequestParam(required = false) String helpDocTypeid,
			@RequestParam(required = false) String title,
			 @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		HelpDocument helpDocument = new HelpDocument();
		helpDocument.setDocumentId(documentId);
		helpDocument.setHelpDocTypeid(helpDocTypeid);
		helpDocument.setDeleteFlag(0);
		helpDocument.setTitle(title);
		List<HelpDocument> list = new ArrayList<HelpDocument>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = helpDocumentService.selectPage(helpDocument);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = helpDocumentService.selectPage(helpDocument);
		}

		int count = helpDocumentService.count(helpDocument);
		// 分页 end

		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<HelpDocument>>(new ArrayList<HelpDocument>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<HelpDocument>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询会计记账
	@ApiOperation(value = "Query HelpDocument", notes = "")
	@RequestMapping(value = "/{documentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<HelpDocument> selectByPrimaryKey(
			@PathVariable("documentId") String documentId) {
		HelpDocument helpDocument = helpDocumentService.selectByPrimaryKey(documentId);
		if (helpDocument == null) {
			return new ResponseRestEntity<HelpDocument>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<HelpDocument>(helpDocument, HttpRestStatus.OK);
	}

	// 新增会计记账
	@ApiOperation(value = "Add HelpDocument", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody HelpDocument helpDocument, BindingResult result,
			UriComponentsBuilder ucBuilder) {

		helpDocument.setDocumentId(IDGenerate.generateCommTwo(IDGenerate.BOOKKEEPKING));
		if (helpDocumentService.isExist(helpDocument)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("helpDocument.create.conflict.message"));
		}

		helpDocument.setDeleteFlag(PageHelperUtil.DELETE_NO);

		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		helpDocumentService.insert(helpDocument);
		//新增日志
     CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, helpDocument,CommonLogImpl.ACCOUNTING);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/helpDocument/{{documentId}}")
				.buildAndExpand(helpDocument.getDocumentId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("helpDocument.create.created.message"));
	}

	// 修改会计记账信息
	@ApiOperation(value = "Edit HelpDocument", notes = "")
	@RequestMapping(value = "{documentId}", method = RequestMethod.PUT)
	public ResponseRestEntity<HelpDocument> update(@PathVariable("documentId") String documentId,
			@Valid @RequestBody HelpDocument helpDocument, BindingResult result) {

		HelpDocument currentHelpDocument = helpDocumentService.selectByPrimaryKey(documentId);

		if (currentHelpDocument == null) {
			return new ResponseRestEntity<HelpDocument>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("helpDocument.update.not_found.message"));
		}

		currentHelpDocument.setHelpDocTypeid(helpDocument.getHelpDocTypeid());
		currentHelpDocument.setTitle(helpDocument.getTitle());
		currentHelpDocument.setContent(helpDocument.getContent());
		currentHelpDocument.setUseNum(helpDocument.getUseNum());
		currentHelpDocument.setUnuseNum(helpDocument.getUnuseNum());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<HelpDocument>(currentHelpDocument,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		helpDocumentService.updateByPrimaryKey(currentHelpDocument);
		//修改日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentHelpDocument,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<HelpDocument>(currentHelpDocument, HttpRestStatus.OK,localeMessageSourceService.getMessage("helpDocument.update.ok.message"));
	}

	// 修改部分会计记账信息
	@ApiOperation(value = "Edit Part HelpDocument", notes = "")
	@RequestMapping(value = "{documentId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<HelpDocument> updateSelective(@PathVariable("documentId") String documentId,
			@RequestBody HelpDocument helpDocument) {

		HelpDocument currentHelpDocument = helpDocumentService.selectByPrimaryKey(documentId);

		if (currentHelpDocument == null) {
			return new ResponseRestEntity<HelpDocument>(HttpRestStatus.NOT_FOUND);
		}
		helpDocument.setDocumentId(documentId);// ?
		helpDocumentService.updateByPrimaryKeySelective(helpDocument);
		//修改日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, helpDocument,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<HelpDocument>(currentHelpDocument, HttpRestStatus.OK);
	}

	// 删除指定会计记账
	@ApiOperation(value = "Delete HelpDocument", notes = "")
	@RequestMapping(value = "/{documentId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<HelpDocument> delete(@PathVariable("documentId") String documentId) {

		HelpDocument helpDocument = helpDocumentService.selectByPrimaryKey(documentId);
		if (helpDocument == null) {
			return new ResponseRestEntity<HelpDocument>(HttpRestStatus.NOT_FOUND);
		}

		helpDocument.setDeleteFlag(1);
		helpDocumentService.updateByPrimaryKeySelective(helpDocument);
		//删除日志开始
		HelpDocument delBean = new HelpDocument();
		delBean.setDocumentId(documentId);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ACCOUNTING);
		//删除日志结束
		return new ResponseRestEntity<HelpDocument>(HttpRestStatus.NO_CONTENT);
	}
}