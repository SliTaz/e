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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultProcessService;
import com.zbensoft.e.payment.db.domain.FraultProcess;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultProcess")
@RestController
public class FraultProcessController {
	@Autowired
	FraultProcessService fraultProcessService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultProcess，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultProcess>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String createTimeStart,
			@RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultProcess fraultProcess = new FraultProcess();
		fraultProcess.setFraultProcessId(id);
		fraultProcess.setName(name);
		fraultProcess.setStatus(status);
		fraultProcess.setCreateTimeStart(createTimeStart);
		fraultProcess.setCreateTimeEnd(createTimeEnd);
		int count = fraultProcessService.count(fraultProcess);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultProcess>>(new ArrayList<FraultProcess>(), HttpRestStatus.NOT_FOUND);
		}
		List<FraultProcess> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultProcessService.selectPage(fraultProcess);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = fraultProcessService.selectPage(fraultProcess);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultProcess>>(new ArrayList<FraultProcess>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultProcess>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultProcess", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultProcess> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultProcess fraultProcess = fraultProcessService.selectByPrimaryKey(id);
		if (fraultProcess == null) {
			return new ResponseRestEntity<FraultProcess>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultProcess>(fraultProcess, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultProcess", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultProcess(@Valid @RequestBody FraultProcess fraultProcess,BindingResult result, UriComponentsBuilder ucBuilder) {

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultProcess.setFraultProcessId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_PROCESS));
		fraultProcess.setCreateTime(PageHelperUtil.getCurrentDate());
		fraultProcessService.insert(fraultProcess);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultProcess,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/fraultProcess/{id}").buildAndExpand(fraultProcess.getFraultProcessId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultProcess", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultProcess> updateFraultProcess(@PathVariable("id") String id,@Valid @RequestBody FraultProcess fraultProcess, BindingResult result) {

		FraultProcess currentFraultProcess = fraultProcessService.selectByPrimaryKey(id);

		if (currentFraultProcess == null) {
			return new ResponseRestEntity<FraultProcess>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentFraultProcess.setName(fraultProcess.getName());
		currentFraultProcess.setStatus(fraultProcess.getStatus());
		currentFraultProcess.setHandleClass(fraultProcess.getHandleClass());
		currentFraultProcess.setCreateTime(fraultProcess.getCreateTime());
		currentFraultProcess.setRemark(fraultProcess.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultProcess>(currentFraultProcess,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultProcessService.updateByPrimaryKey(currentFraultProcess);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultProcess,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultProcess>(currentFraultProcess, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultProcess", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultProcess> updateFraultProcessSelective(@PathVariable("id") String id,
			@RequestBody FraultProcess fraultProcess) {

		FraultProcess currentFraultProcess = fraultProcessService.selectByPrimaryKey(id);

		if (currentFraultProcess == null) {
			return new ResponseRestEntity<FraultProcess>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultProcess.setFraultProcessId(id);
		currentFraultProcess.setName(fraultProcess.getName());
		currentFraultProcess.setStatus(fraultProcess.getStatus());
		currentFraultProcess.setHandleClass(fraultProcess.getHandleClass());
		currentFraultProcess.setCreateTime(fraultProcess.getCreateTime());
		currentFraultProcess.setRemark(fraultProcess.getRemark());
		fraultProcessService.updateByPrimaryKeySelective(currentFraultProcess);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultProcess,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultProcess>(currentFraultProcess, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultProcess", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultProcess> deleteFraultProcess(@PathVariable("id") String id) {

		FraultProcess fraultProcess = fraultProcessService.selectByPrimaryKey(id);
		if (fraultProcess == null) {
			return new ResponseRestEntity<FraultProcess>(HttpRestStatus.NOT_FOUND);
		}

		fraultProcessService.deleteByPrimaryKey(id);
		//删除日志开始
		FraultProcess delBean = new FraultProcess();
		delBean.setFraultProcessId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultProcess>(HttpRestStatus.NO_CONTENT);
	}
}