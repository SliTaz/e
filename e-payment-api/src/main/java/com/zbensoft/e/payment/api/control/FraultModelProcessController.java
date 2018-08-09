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

import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultModelProcessService;
import com.zbensoft.e.payment.db.domain.FraultModelProcess;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultModelProcess")
@RestController
public class FraultModelProcessController {
	@Autowired
	FraultModelProcessService fraultModelProcessService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query FraultModelProcess，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultModelProcess>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		FraultModelProcess fraultModelProcess = new FraultModelProcess();
		List<FraultModelProcess> list = fraultModelProcessService.selectPage(fraultModelProcess);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultModelProcess>>(new ArrayList<FraultModelProcess>(),
					HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultModelProcess>>(list, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Query FraultModelProcess", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultProcessId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultModelProcess> selectByPrimaryKey(@PathVariable("fraultModelId") String fraultModelId,
			@PathVariable("fraultProcessId") String fraultProcessId) {
		FraultModelProcess bean = new FraultModelProcess();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultProcessId(fraultProcessId);
		FraultModelProcess fraultModelProcess = fraultModelProcessService.selectByPrimaryKey(bean);
		if (fraultModelProcess == null) {
			return new ResponseRestEntity<FraultModelProcess>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultModelProcess>(fraultModelProcess, HttpRestStatus.OK);
	}
	
	
	@ApiOperation(value = "Query FraultModelProcess", notes = "")
	@RequestMapping(value = "/{fraultProcessId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<FraultModelProcess>> selectByPayAppId(@PathVariable("fraultProcessId") String fraultProcessId) {
		List<FraultModelProcess> list = fraultModelProcessService.selectByModelId(fraultProcessId);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<FraultModelProcess>>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<FraultModelProcess>>(list, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultModelProcess", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createFraultModelProcess(@Valid @RequestBody FraultModelProcess fraultModelProcess,
			BindingResult result, UriComponentsBuilder ucBuilder) {
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultModelProcessService.insert(fraultModelProcess);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, fraultModelProcess,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(
				ucBuilder.path("/fraultModelProcess/{id}").buildAndExpand(fraultModelProcess.getFraultModelId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultModelProcess", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultProcessId}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultModelProcess> updateFraultModelProcess(
			@PathVariable("fraultModelId") String fraultModelId, @PathVariable("fraultProcessId") String fraultProcessId,
			@Valid @RequestBody FraultModelProcess fraultModelProcess, BindingResult result) {
		FraultModelProcess bean = new FraultModelProcess();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultProcessId(fraultProcessId);
		FraultModelProcess currentFraultModelProcess = fraultModelProcessService.selectByPrimaryKey(bean);

		if (currentFraultModelProcess == null) {
			return new ResponseRestEntity<FraultModelProcess>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentFraultModelProcess.setFraultModelId(fraultModelProcess.getFraultModelId());
		currentFraultModelProcess.setFraultProcessId(fraultModelProcess.getFraultModelId());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<FraultModelProcess>(currentFraultModelProcess,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}
		fraultModelProcessService.updateByPrimaryKey(currentFraultModelProcess);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultModelProcess,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultModelProcess>(currentFraultModelProcess, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part AlarmType", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultProcessId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultModelProcess> updateFraultModelProcessSelective(@PathVariable("fraultModelId") String fraultModelId, 
			@PathVariable("fraultProcessId") String fraultProcessId,@RequestBody FraultModelProcess fraultModelProcess) {
		FraultModelProcess bean =new FraultModelProcess();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultProcessId(fraultProcessId);
		FraultModelProcess currentFraultModelProcess = fraultModelProcessService.selectByPrimaryKey(bean);

		if (currentFraultModelProcess == null) {
			return new ResponseRestEntity<FraultModelProcess>(HttpRestStatus.NOT_FOUND);
		}
		currentFraultModelProcess.setFraultModelId(fraultModelProcess.getFraultModelId());
		currentFraultModelProcess.setFraultProcessId(fraultModelProcess.getFraultProcessId());
		fraultModelProcessService.updateByPrimaryKeySelective(currentFraultModelProcess);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentFraultModelProcess,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultModelProcess>(currentFraultModelProcess, HttpRestStatus.OK);
	}
	
	
	@ApiOperation(value = "Delete FraultModelProcess", notes = "")
	@RequestMapping(value = "/{fraultModelId}/{fraultProcessId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultModelProcess> deleteFraultModelProcess(
			@PathVariable("fraultModelId") String fraultModelId,
			@PathVariable("fraultProcessId") String fraultProcessId) {
		FraultModelProcess bean = new FraultModelProcess();
		bean.setFraultModelId(fraultModelId);
		bean.setFraultProcessId(fraultProcessId);
		FraultModelProcess fraultModelProcess = fraultModelProcessService.selectByPrimaryKey(bean);
		if (fraultModelProcess == null) {
			return new ResponseRestEntity<FraultModelProcess>(HttpRestStatus.NOT_FOUND);
		}
		fraultModelProcessService.deleteByPrimaryKey(fraultModelProcess);
		//删除日志开始
		FraultModelProcess delBean = new FraultModelProcess();
		delBean.setFraultModelId(fraultModelId);
		delBean.setFraultProcessId(fraultProcessId);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultModelProcess>(HttpRestStatus.NO_CONTENT);
	}
}