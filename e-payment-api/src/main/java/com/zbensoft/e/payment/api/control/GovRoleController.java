package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.GovMenuService;
import com.zbensoft.e.payment.api.service.api.GovRoleService;
import com.zbensoft.e.payment.db.domain.GovMenu;
import com.zbensoft.e.payment.db.domain.GovRole;
import com.zbensoft.e.payment.db.domain.SysRole;
import com.zbensoft.e.payment.db.domain.ZTreeNode;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/govrole")
@RestController
public class GovRoleController {
	
	private static final Logger log = LoggerFactory.getLogger(GovRoleController.class);

	@Autowired
	GovRoleService govRoleService;
	@Autowired
	GovMenuService govMenuService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	

	@PreAuthorize("hasRole('R_GOV_U_R_Q')")
	@ApiOperation(value = "Query govrole,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GovRole>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) String roleId,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		GovRole role = new GovRole();
		role.setRoleId(id);
		role.setName(name);
		role.setRemark(remark);


		int count = govRoleService.count(role);
		if (count == 0) {
			return new ResponseRestEntity<List<GovRole>>(new ArrayList<GovRole>(), HttpRestStatus.NOT_FOUND);
		}

		List<GovRole> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = govRoleService.selectPage(role);
		} else {
			list = govRoleService.selectPage(role);
		}

		return new ResponseRestEntity<List<GovRole>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "Add govrole", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createRole(@RequestBody GovRole role, BindingResult result, UriComponentsBuilder ucBuilder) {
		role.setRoleId(IDGenerate.generateCommOne(IDGenerate.GOV_ROLE));
		if (govRoleService.isRoleExist(role)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		govRoleService.insert(role);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, role,CommonLogImpl.GOV_USER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/role/{id}").buildAndExpand(role.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "Edit govrole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovRole> updateRole(@PathVariable("id") String id, @RequestBody SysRole role) {

		GovRole currentRole = govRoleService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<GovRole>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentRole.setRoleId(role.getRoleId());
		currentRole.setCode(role.getCode());
		currentRole.setName(role.getName());
		currentRole.setRemark(role.getRemark());
		govRoleService.updateByPrimaryKey(currentRole);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentRole,CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovRole>(currentRole, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "Edit Part govrole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GovRole> updateRoleSelective(@PathVariable("id") String id, @RequestBody SysRole role) {

		GovRole currentRole = govRoleService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<GovRole>(HttpRestStatus.NOT_FOUND);
		}
		govRoleService.updateByPrimaryKeySelective(currentRole);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentRole,CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovRole>(currentRole, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "Delete govrole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovRole> deleteRole(@PathVariable("id") String id) {

		GovRole role = govRoleService.selectByPrimaryKey(id);
		if (role == null) {
			return new ResponseRestEntity<GovRole>(HttpRestStatus.NOT_FOUND);
		}

		govRoleService.deleteByPrimaryKey(id);
		//删除日志开始
		GovRole gov = new GovRole();
				gov.setRoleId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, gov,CommonLogImpl.GOV_USER);
			//删除日志结束
		return new ResponseRestEntity<GovRole>(HttpRestStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('R_GOV_U_R_Q')")
	@RequestMapping(value = "/permissioRole", method = RequestMethod.GET)
	public ResponseEntity<GovRole> permissioRole(@RequestParam(required = false) String id) {
		//System.out.println("id:"+id);
		List<GovMenu> resources = govMenuService.getRoleResources(id);
		//System.out.println("resources 条数:"+resources.size());
		
		List<GovMenu> allRes = govMenuService.findAll();
		//System.out.println("allRes 条数:"+allRes.size());
		
		StringBuffer sb = new StringBuffer();
        sb.append("var zNodes = [];");
        
        List<ZTreeNode> zTreeNodeList=new ArrayList<ZTreeNode>();
        
        for (GovMenu r : allRes) {
            boolean flag = false;
            for (GovMenu ur : resources) {//用户拥有的权限
                if (ur.getMenuId().equals(r.getMenuId())) {
                	ZTreeNode zTreeNode=new ZTreeNode();
                	
                    sb.append("zNodes.push({ id: '"
                            + r.getMenuId() + "', pId: '"
                            + r.getPreMenuId()
                            + "', name: '" + r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs()
                            + "',checked: true});");//ztree勾选状态
                    flag = true;
                    
                    zTreeNode.setId(r.getMenuId());
                    zTreeNode.setpId(r.getPreMenuId());
                    zTreeNode.setName(r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs());
                    zTreeNode.setChecked(true);
                    zTreeNodeList.add(zTreeNode);
                }
            }
            if (!flag) {
                sb.append("zNodes.push({ id: '"
                        + r.getMenuId() + "', pId: '"
                        + r.getPreMenuId()
                        + "', name: '" + r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs()
                        + "',checked: false});");//ztree不勾选状态
                
                ZTreeNode zTreeNode=new ZTreeNode();
                zTreeNode.setId(r.getMenuId());
                zTreeNode.setpId(r.getPreMenuId());
                zTreeNode.setName(r.getMenuNameCn()+","+r.getMenuNameEn()+","+r.getMenuNameEs());
                zTreeNode.setChecked(false);
                zTreeNodeList.add(zTreeNode);

            }
        }
        
        //System.out.println("resources:"+sb);
        
        //Map<String, Object> modelMap=new HashMap<String, Object>();
        //modelMap.put("roleId", id);
        //modelMap.put("resources", sb);
        //System.out.println("测试了是否正常路径11");
        //ModelAndView mv = new ModelAndView("http://localhost:9090/permissioRole.html",modelMap);
        
        //return mv;
        
        GovRole role=new GovRole();
        role.setRoleId(id);
        role.setzTreeNodes(zTreeNodeList);
        
        return new ResponseEntity<GovRole>(role,HttpStatus.OK);
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@RequestMapping(value = "/saveRoleRescoursForRole", method = RequestMethod.POST)
	public ResponseRestEntity<Void> saveRoleRescoursForRole(@RequestParam(required = false) String roleId,
			@RequestParam(required = false) String rescId) {
		//System.out.println("roleId:"+roleId+";rescId:"+rescId);
		
		List<String> list = CommonFun.removeSameItem(Arrays.asList(rescId.split(",")));
		
		try {
			govMenuService.saveRoleRescours(roleId, list);
		} catch (Exception e) {
			log.error("",e);
			return new ResponseRestEntity<Void>(HttpRestStatus.UNKNOWN,localeMessageSourceService.getMessage("common.failed"));
		}
		return new ResponseRestEntity<Void>(HttpRestStatus.OK,localeMessageSourceService.getMessage("common.success"));
		
	}
}