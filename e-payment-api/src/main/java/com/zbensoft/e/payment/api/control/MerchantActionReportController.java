package com.zbensoft.e.payment.api.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.service.api.MerchantActiveReportService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.MerchantActiveReport;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantActionReport")
@RestController
public class MerchantActionReportController {

	@Autowired
	MerchantActiveReportService merchantActiveReportService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;


	// 查询应用，支持分页
	@PreAuthorize("hasRole('R_BI_SAS_Q')")
	@ApiOperation(value = "Query application, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantActiveReport>> selectPage(@RequestParam(required = false) String statisticsTimeStart,@RequestParam(required = false) String statisticsTimeEnd, @RequestParam(required = false) Long totalActiveUserNum,
			@RequestParam(required = false) String fileAddress, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		MerchantActiveReport merchantActiveReport = new MerchantActiveReport();
		merchantActiveReport.setStatisticsTimeStart(statisticsTimeStart);
		merchantActiveReport.setStatisticsTimeEnd(statisticsTimeEnd);
		merchantActiveReport.setTotalActiveUserNum(totalActiveUserNum);
		merchantActiveReport.setFileAddress(fileAddress);

		List<MerchantActiveReport> list = merchantActiveReportService.selectPage(merchantActiveReport);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantActiveReportService.selectPage(merchantActiveReport);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantActiveReportService.selectPage(merchantActiveReport);
		}

		int count = merchantActiveReportService.count(merchantActiveReport);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantActiveReport>>(new ArrayList<MerchantActiveReport>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantActiveReport>>(list, HttpRestStatus.OK, count, count);

	}

	// 删除指定应用
	@PreAuthorize("hasRole('R_BI_SAS_E')")
	@ApiOperation(value = "Delete the specified consumptionType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantActiveReport> deleteHelpDocType(@PathVariable("id") String statisticsTime) {

		int result = merchantActiveReportService.deleteByPrimaryKey(statisticsTime);
		if (result == 0) {
			return new ResponseRestEntity<MerchantActiveReport>(HttpRestStatus.NOT_FOUND);
		}
		// 删除日志开始
		MerchantActiveReport delBean = new MerchantActiveReport();
		delBean.setStatisticsTime(statisticsTime);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean, CommonLogImpl.BI_REPORT);
		// 删除日志结束
		return new ResponseRestEntity<MerchantActiveReport>(HttpRestStatus.NO_CONTENT);
	}

	// 批量
	@PreAuthorize("hasRole('R_BI_SAS_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantActiveReport> deleteSysLogMany(@PathVariable("id") String id) {
		String[] idStr = id.split(",");
		if (idStr != null) {
			for (String str : idStr) {
				merchantActiveReportService.deleteByPrimaryKey(str);
			}
		}
		return new ResponseRestEntity<MerchantActiveReport>(HttpRestStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('R_BI_SAS_Q')")
	@ApiOperation(value = "download errorLog", notes = "")
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public String hello(@RequestParam(required = false) String fileAddress, HttpServletResponse res) throws IOException {

		String REPORT_SELLER_ACTIVE_STAT_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_FILE_PATH);
		String fileName = fileAddress;
		File readPath = new File(REPORT_SELLER_ACTIVE_STAT_FILE_PATH);
		if (!readPath.exists() && !readPath.isDirectory()) {
			readPath.mkdirs();
		}

		File readAllPath = new File(REPORT_SELLER_ACTIVE_STAT_FILE_PATH + fileName);
		
		res.setHeader("content-type", "application/octet-stream");
		res.setContentType("application/octet-stream");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName);

		ServletOutputStream out = res.getOutputStream();
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(readAllPath));
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
		return bos.toString();

	}

}