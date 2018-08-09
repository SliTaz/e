package com.zbensoft.e.payment.api.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantGroupService;
import com.zbensoft.e.payment.api.service.api.MerchantGroupUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.common.util.ImportUtil;
import com.zbensoft.e.payment.db.domain.MerchantGroup;
import com.zbensoft.e.payment.db.domain.MerchantGroupUserKey;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;
@RequestMapping(value = "/merchantGroup")
@RestController
public class MerchantGroupController {
	
	private static final Logger log = LoggerFactory.getLogger(MerchantGroupController.class);

	@Autowired
	MerchantGroupService merchantGroupService;
	
	@Autowired
	MerchantGroupUserService merchantGroupUserService;
	
	@Autowired
	MerchantUserService merchantUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@Value("${upload.file.tmp.folder}")
	private String UPLOAD_FILE_FOLDER;
	
	
	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_SELLER_G_Q')")
	@ApiOperation(value = "Query merchantGroup, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantGroup>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String name, 
			@RequestParam(required = false) String status, 
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		MerchantGroup merchantGroup = new MerchantGroup();
		merchantGroup.setMerchantGroupId(id);
		merchantGroup.setName(name);
		try {
			if (status == null || "".equals(status) || "undefined".equals(status)) {

			} else {
				merchantGroup.setStatus(Integer.valueOf(status));
			}

		} catch (Exception e) {
			log.error("",e);
		}
		if(idNumber!=null&&!"".equals(idNumber)){
			MerchantUser  merchantUser = merchantUserService.selectByIdNumber(idNumber);
			if(merchantUser==null){
				return new ResponseRestEntity<List<MerchantGroup>>(new ArrayList<MerchantGroup>(), HttpRestStatus.NOT_FOUND);
			}
			if(merchantUser!=null){
				List<MerchantGroupUserKey> groupUserList = merchantGroupUserService.selectByUserId(merchantUser.getUserId());
				if(groupUserList!=null&&groupUserList.size()>0){
				String[] userIds = null;
					userIds = new String[groupUserList.size()];
					for (int i = 0; i < groupUserList.size(); i++) {
						userIds[i] = groupUserList.get(i).getMerchantGroupId();
					}
				
				merchantGroup.setUserIds(userIds);
				}else{
					return new ResponseRestEntity<List<MerchantGroup>>(new ArrayList<MerchantGroup>(), HttpRestStatus.NOT_FOUND);
				}
			}
		}

		int count = merchantGroupService.count(merchantGroup);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantGroup>>(new ArrayList<MerchantGroup>(), HttpRestStatus.NOT_FOUND);
		}

		List<MerchantGroup> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantGroupService.selectPage(merchantGroup);
		} else {
			list = merchantGroupService.selectPage(merchantGroup);
		}

		return new ResponseRestEntity<List<MerchantGroup>>(list, HttpRestStatus.OK, count, count);
	}
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "Add merchantGroup", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantGroup(@Valid @RequestBody MerchantGroup merchantGroup, BindingResult result, UriComponentsBuilder ucBuilder) {
		merchantGroup.setMerchantGroupId(IDGenerate.generateMERCHANT_USER_ID());
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		// 是否存在相同用户名
		if (merchantGroupService.isNameExist(merchantGroup)) {//可以相同
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
         //新增
		merchantGroupService.insert(merchantGroup);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantGroup,CommonLogImpl.MERCHANT);
         //返回处理
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantGroup/{id}").buildAndExpand(merchantGroup.getMerchantGroupId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}


	// 修改应用信息
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "Edit merchantGroup", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantGroup> updateMerchantGroup(@PathVariable("id") String id,@Valid @RequestBody MerchantGroup merchantGroup, BindingResult result) {

		MerchantGroup currenMerchantGroup = merchantGroupService.selectByPrimaryKey(id);

		if (currenMerchantGroup == null) {
			return new ResponseRestEntity<MerchantGroup>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currenMerchantGroup.setName(merchantGroup.getName());
		currenMerchantGroup.setStatus(merchantGroup.getStatus());
		currenMerchantGroup.setRemark(merchantGroup.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			/*for (ObjectError error : list) {
				System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}*/

			return new ResponseRestEntity<MerchantGroup>(currenMerchantGroup, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantGroupService.updateByPrimaryKey(currenMerchantGroup);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currenMerchantGroup,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantGroup>(currenMerchantGroup, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分应用信息
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "Edit Part merchantGroup", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantGroup> updateMerchantGroupSelective(@PathVariable("id") String id, @RequestBody MerchantGroup merchantGroup) {

		MerchantGroup currenMerchantGroup = merchantGroupService.selectByPrimaryKey(id);

		if (currenMerchantGroup == null) {
			return new ResponseRestEntity<MerchantGroup>(HttpRestStatus.NOT_FOUND);
		}
		merchantGroup.setMerchantGroupId(id);
		merchantGroupService.updateByPrimaryKeySelective(merchantGroup);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantGroup,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantGroup>(currenMerchantGroup, HttpRestStatus.OK);
	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "Delete merchantGroup", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantGroup> deleteMerchantGroup(@PathVariable("id") String id) {

		MerchantGroup merchantGroup = merchantGroupService.selectByPrimaryKey(id);
		if (merchantGroup == null) {
			return new ResponseRestEntity<MerchantGroup>(HttpRestStatus.NOT_FOUND);
		}

		//删除中间表
		merchantGroupUserService.deleteByGroupId(id);
		
		merchantGroupService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantGroup merchant = new MerchantGroup();
		merchant.setMerchantGroupId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantGroup>(HttpRestStatus.NO_CONTENT);
	}

	// 启用
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "statusEnable", notes = "")
	@RequestMapping(value = "/statusEnable", method = RequestMethod.GET)
	public ResponseRestEntity<Void> statusEnable(@RequestParam(required = false) String id) {
		merchantGroupService.statusEnable(id);
		//修改日志
				MerchantGroup bean =new MerchantGroup();
				bean.setMerchantGroupId(id);
				bean.setStatus(MessageDef.STATUS.ENABLE_INT);
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, bean,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}

	// 停用
	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@ApiOperation(value = "statusDisable", notes = "")
	@RequestMapping(value = "/statusDisable", method = RequestMethod.GET)
	public ResponseRestEntity<Void> statusDisable(@RequestParam(required = false) String id) {
		merchantGroupService.statusDisable(id);
		//修改日志
		MerchantGroup bean =new MerchantGroup();
				bean.setMerchantGroupId(id);
				bean.setStatus(MessageDef.STATUS.DISABLE_INT);
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, bean,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<Void>(HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_G_E')")
	@RequestMapping(value="/singleUpload", method = RequestMethod.POST)
	public Map<String,Object> singleFileUpload(HttpServletRequest request,@RequestParam("name") String name,
			@RequestParam("merchantGroupId") String merchantGroupId,@RequestParam("flag") String flag) throws Exception {
		//System.out.println("flag:"+flag);//true表示删除之前全部数据，false表示不删除之前全部数据
		
		boolean deleteFlagAll = false;

		if (!CommonFun.isEmpty(flag)) {
			if (flag.equals("true")) {
				deleteFlagAll = true;
			}
		}
		
		String filePre=System.currentTimeMillis()+"";
		name=filePre+"_"+name;
		
		//System.out.println("after name:"+name);
		//System.out.println("merchantGroupId:"+merchantGroupId);
		
		String path = UPLOAD_FILE_FOLDER;//request.getSession().getServletContext().getRealPath("upload"); 
		//System.out.println("path:"+path);
		
        File targetFile = new File(path, name);
        if(!targetFile.getParentFile().exists()){  
            targetFile.getParentFile().mkdirs();  
        }
        
        write(path,name,request.getInputStream());
        
        int import_int=insertToDB(targetFile,merchantGroupId,deleteFlagAll);
        Map<String,Object> result_map=new HashMap<String,Object>();
        result_map.put("importNum",import_int);
		return result_map;
	}
	
	private void write(String path,String filename, InputStream in) {
		//System.out.println("写入文件");

		File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) {// 若创建文件夹不成功
				//System.out.println("Unable to create external cache directory");
			}
		}

		File targetfile = new File(path + filename);
		OutputStream os = null;
		try {
			os = new FileOutputStream(targetfile);
			int ch = 0;
			while ((ch = in.read()) != -1) {
				os.write(ch);
			}
			os.flush();
		} catch (Exception e) {
			log.error("",e);
		} finally {
			try {
				os.close();
				in.close();
			} catch (Exception e) {
				log.error("",e);
			}
		}
	}
	
	
	private int insertToDB(File file,String merchantGroupId,boolean deleteFlagAll){
		//System.out.println("准备写入数据库中...字符集");
		try {
			//BufferedReader in =new BufferedReader(new FileReader(file));
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader in = new BufferedReader(isr);
			List<String> list = new ArrayList<String>();
			String s;
			int line_num=0;
			while((s=in.readLine())!=null){
				line_num=line_num+1;
				String[] str = s.split(",");
				if(str!=null){
					String userId = null;
					if(str.length==1){
						userId = str[0];
						
						if(!CommonFun.isEmpty(userId)){
							userId=userId.trim();
							
							//为了解决txt中的第一行的问题 start
							//例如第一行数据是1按照常规来看长度应该是1但是长度显示是2 故需要去除第一位得到后面的数据才是真实的数据
							if(line_num==1&&userId.length()>=2){
								userId=ImportUtil.getValueForUTF_8(userId);
							}
							//为了解决txt中的第一行的问题 end
							
							list.add(userId.trim());
						}
						
					}
				}
			}
			//System.out.println("条数："+list.size());
			int import_int=merchantGroupService.saveOrUpdate(list,merchantGroupId,deleteFlagAll);
			CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPORT, list,CommonLogImpl.MERCHANT);
			return import_int;
		} catch (Exception e) {
			log.error("",e);
		}
		return 0;
	}
}
