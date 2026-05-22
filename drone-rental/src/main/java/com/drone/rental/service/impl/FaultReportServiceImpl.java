package com.drone.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.common.Constants;
import com.drone.rental.common.ResultCode;
import com.drone.rental.common.exception.BusinessException;
import com.drone.rental.dto.AuditDTO;
import com.drone.rental.dto.FaultReportDTO;
import com.drone.rental.entity.Drone;
import com.drone.rental.entity.FaultReport;
import com.drone.rental.entity.RentalOrder;
import com.drone.rental.mapper.FaultReportMapper;
import com.drone.rental.security.UserContext;
import com.drone.rental.service.DroneService;
import com.drone.rental.service.FaultReportService;
import com.drone.rental.service.MaintenanceTicketService;
import com.drone.rental.service.OrderService;
import com.drone.rental.service.UserService;
import com.drone.rental.vo.FaultReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 故障上报服务实现类
 */
@Service
public class FaultReportServiceImpl extends ServiceImpl<FaultReportMapper, FaultReport>
        implements FaultReportService {

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private MaintenanceTicketService maintenanceTicketService;

    @Autowired
    @Lazy
    private OrderService orderService;

    @Autowired
    private DroneService droneService;

    @Override
    public void reportFault(FaultReportDTO dto) {
        Long userId = UserContext.getCurrentUserId();

        // 检查用户状态
        userService.checkUserOperable(userId);

        FaultReport report = new FaultReport();
        report.setReportNo(generateReportNo());
        report.setUserId(userId);
        report.setDroneId(dto.getDroneId());
        report.setOrderId(dto.getOrderId());
        report.setFaultType(dto.getFaultType());
        report.setFaultDescription(dto.getFaultDescription());
        report.setFaultImages(dto.getFaultImages());
        report.setFaultTime(dto.getFaultTime() != null ? dto.getFaultTime() : LocalDateTime.now());
        report.setAuditStatus(Constants.FAULT_AUDIT_PENDING);

        this.save(report);
    }

    @Override
    public IPage<FaultReport> getCurrentUserFaultReports(Integer pageNum, Integer pageSize) {
        Long userId = UserContext.getCurrentUserId();
        Page<FaultReport> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<FaultReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultReport::getUserId, userId);
        wrapper.orderByDesc(FaultReport::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<FaultReportVO> getCurrentUserFaultReportsVO(Integer pageNum, Integer pageSize) {
        IPage<FaultReport> faultPage = getCurrentUserFaultReports(pageNum, pageSize);

        Page<FaultReportVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(faultPage.getTotal());
        voPage.setRecords(faultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public List<FaultReportVO> getFaultReportsByOrderId(Long orderId) {
        LambdaQueryWrapper<FaultReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultReport::getOrderId, orderId);
        wrapper.orderByDesc(FaultReport::getCreatedTime);

        return this.list(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 将FaultReport转换为FaultReportVO
     */
    private FaultReportVO convertToVO(FaultReport report) {
        FaultReportVO vo = new FaultReportVO();
        vo.setId(report.getId());
        vo.setReportNo(report.getReportNo());
        vo.setUserId(report.getUserId());
        vo.setDroneId(report.getDroneId());
        vo.setOrderId(report.getOrderId());
        vo.setFaultType(report.getFaultType());
        vo.setFaultDescription(report.getFaultDescription());
        vo.setFaultImages(report.getFaultImages());
        vo.setFaultTime(report.getFaultTime());
        vo.setAuditStatus(report.getAuditStatus());
        vo.setAuditRemark(report.getAuditRemark());
        vo.setAuditTime(report.getAuditTime());
        vo.setCreatedTime(report.getCreatedTime());

        // 获取用户信息
        if (report.getUserId() != null) {
            com.drone.rental.entity.User user = userService.getById(report.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                vo.setContactPhone(user.getPhone());
            }
        }

        // 获取订单信息
        if (report.getOrderId() != null) {
            RentalOrder order = orderService.getById(report.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
            }
        }

        // 获取无人机信息
        if (report.getDroneId() != null) {
            Drone drone = droneService.getById(report.getDroneId());
            if (drone != null) {
                vo.setDroneModel(drone.getModel());
                vo.setDroneBrand(drone.getBrand());
            }
        }

        return vo;
    }

    @Override
    public IPage<FaultReport> pageFaultReports(Integer pageNum, Integer pageSize, Integer auditStatus) {
        Page<FaultReport> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<FaultReport> wrapper = new LambdaQueryWrapper<>();
        if (auditStatus != null) {
            wrapper.eq(FaultReport::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(FaultReport::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<FaultReportVO> pageFaultReportsVO(Integer pageNum, Integer pageSize, Integer auditStatus) {
        IPage<FaultReport> faultPage = pageFaultReports(pageNum, pageSize, auditStatus);

        Page<FaultReportVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(faultPage.getTotal());
        voPage.setRecords(faultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditFaultReport(Long id, AuditDTO dto) {
        FaultReport report = this.getById(id);
        if (report == null) {
            throw new BusinessException(ResultCode.FAULT_NOT_EXIST);
        }

        if (report.getAuditStatus() != Constants.FAULT_AUDIT_PENDING) {
            throw new BusinessException(ResultCode.FAULT_ALREADY_PROCESSED);
        }

        report.setAuditStatus(dto.getAuditStatus());
        report.setAuditRemark(dto.getAuditRemark());
        report.setAuditTime(LocalDateTime.now());
        report.setAuditorId(UserContext.getCurrentUserId());

        this.updateById(report);

        // 如果确认故障，自动生成维修工单
        if (dto.getAuditStatus() == Constants.FAULT_AUDIT_CONFIRMED) {
            maintenanceTicketService.createFromFaultReport(report);
        }
    }

    @Override
    public FaultReport getFaultReportDetail(Long id) {
        FaultReport report = this.getById(id);
        if (report == null) {
            throw new BusinessException(ResultCode.FAULT_NOT_EXIST);
        }
        return report;
    }

    @Override
    public FaultReportVO getFaultReportDetailVO(Long id) {
        FaultReport report = getFaultReportDetail(id);
        return convertToVO(report);
    }

    /**
     * 生成故障单号
     */
    private String generateReportNo() {
        return "FLT" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }
}
