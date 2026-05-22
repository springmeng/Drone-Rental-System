package com.drone.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.drone.rental.common.Constants;
import com.drone.rental.common.ResultCode;
import com.drone.rental.common.exception.BusinessException;
import com.drone.rental.dto.MaintenanceTicketDTO;
import com.drone.rental.entity.Drone;
import com.drone.rental.entity.FaultReport;
import com.drone.rental.entity.MaintenanceTicket;
import com.drone.rental.entity.User;
import com.drone.rental.mapper.FaultReportMapper;
import com.drone.rental.mapper.MaintenanceTicketMapper;
import com.drone.rental.service.DroneService;
import com.drone.rental.service.MaintenanceTicketService;
import com.drone.rental.service.UserService;
import com.drone.rental.vo.MaintenanceTicketVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 维修工单服务实现类
 */
@Service
public class MaintenanceTicketServiceImpl extends ServiceImpl<MaintenanceTicketMapper, MaintenanceTicket>
        implements MaintenanceTicketService {

    @Autowired
    private DroneService droneService;

    @Autowired
    private UserService userService;

    @Autowired
    private FaultReportMapper faultReportMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFromFaultReport(FaultReport faultReport) {
        MaintenanceTicket ticket = new MaintenanceTicket();
        ticket.setTicketNo(generateTicketNo());
        ticket.setFaultReportId(faultReport.getId());
        ticket.setDroneId(faultReport.getDroneId());
        ticket.setUserId(faultReport.getUserId());
        ticket.setMaintenanceDescription(faultReport.getFaultDescription());
        ticket.setStatus(Constants.MAINTENANCE_STATUS_PENDING);

        this.save(ticket);

        // 将无人机状态设为维护中
        droneService.updateStatus(faultReport.getDroneId(), Constants.DRONE_STATUS_MAINTENANCE);
    }

    @Override
    public IPage<MaintenanceTicket> pageMaintenanceTickets(Integer pageNum, Integer pageSize,
                                                            Integer status, Long droneId, String droneModel, String type) {
        Page<MaintenanceTicket> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<MaintenanceTicket> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(MaintenanceTicket::getStatus, status);
        }
        if (droneId != null) {
            wrapper.eq(MaintenanceTicket::getDroneId, droneId);
        }
        // 根据设备型号查询 - 先查出符合条件的droneId列表
        if (StringUtils.hasText(droneModel)) {
            List<Long> droneIds = droneService.list(
                new LambdaQueryWrapper<Drone>().like(Drone::getModel, droneModel)
            ).stream().map(Drone::getId).collect(java.util.stream.Collectors.toList());
            if (droneIds.isEmpty()) {
                return new Page<>(pageNum, pageSize);
            }
            wrapper.in(MaintenanceTicket::getDroneId, droneIds);
        }
        // 根据维修类型查询
        if (StringUtils.hasText(type)) {
            wrapper.eq(MaintenanceTicket::getMaintenanceType, type);
        }
        wrapper.orderByDesc(MaintenanceTicket::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public IPage<MaintenanceTicketVO> pageMaintenanceTicketsVO(Integer pageNum, Integer pageSize,
                                                                Integer status, Long droneId, String droneModel, String type) {
        // 先获取原始分页数据
        IPage<MaintenanceTicket> ticketPage = pageMaintenanceTickets(pageNum, pageSize, status, droneId, droneModel, type);

        // 转换为VO
        Page<MaintenanceTicketVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(ticketPage.getTotal());
        voPage.setRecords(ticketPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public MaintenanceTicketVO getTicketDetailVO(Long id) {
        MaintenanceTicket ticket = getTicketDetail(id);
        return convertToVO(ticket);
    }

    /**
     * 将实体转换为VO
     */
    private MaintenanceTicketVO convertToVO(MaintenanceTicket ticket) {
        MaintenanceTicketVO vo = new MaintenanceTicketVO();
        BeanUtils.copyProperties(ticket, vo);

        // 获取无人机信息
        if (ticket.getDroneId() != null) {
            Drone drone = droneService.getById(ticket.getDroneId());
            if (drone != null) {
                vo.setDroneModel(drone.getModel());
                vo.setDroneBrand(drone.getBrand());
            }
        }

        // 获取用户信息
        if (ticket.getUserId() != null) {
            User user = userService.getById(ticket.getUserId());
            if (user != null) {
                vo.setUserName(user.getUsername());
            }
        }

        // 获取故障类型（从关联的故障报修）
        if (ticket.getFaultReportId() != null) {
            FaultReport faultReport = faultReportMapper.selectById(ticket.getFaultReportId());
            if (faultReport != null) {
                vo.setFaultType(faultReport.getFaultType());
            }
        }

        return vo;
    }

    @Override
    public void updateMaintenanceTicket(Long id, MaintenanceTicketDTO dto) {
        MaintenanceTicket ticket = this.getById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.MAINTENANCE_NOT_EXIST);
        }

        if (StringUtils.hasText(dto.getMaintenanceType())) {
            ticket.setMaintenanceType(dto.getMaintenanceType());
        }
        if (StringUtils.hasText(dto.getMaintenanceDescription())) {
            ticket.setMaintenanceDescription(dto.getMaintenanceDescription());
        }
        if (dto.getStatus() != null) {
            ticket.setStatus(dto.getStatus());
        }
        if (dto.getEstimatedCost() != null) {
            ticket.setEstimatedCost(dto.getEstimatedCost());
        }
        if (dto.getActualCost() != null) {
            ticket.setActualCost(dto.getActualCost());
        }
        if (dto.getEstimatedDays() != null) {
            ticket.setEstimatedDays(dto.getEstimatedDays());
        }
        if (dto.getActualDays() != null) {
            ticket.setActualDays(dto.getActualDays());
        }
        if (StringUtils.hasText(dto.getAssigneeName())) {
            ticket.setAssigneeName(dto.getAssigneeName());
        }

        // 添加进度备注
        if (StringUtils.hasText(dto.getProgressNote())) {
            String currentNotes = ticket.getProgressNotes();
            String newNote = LocalDateTime.now() + ": " + dto.getProgressNote();
            if (StringUtils.hasText(currentNotes)) {
                ticket.setProgressNotes(currentNotes + "\n" + newNote);
            } else {
                ticket.setProgressNotes(newNote);
            }
        }

        this.updateById(ticket);
    }

    @Override
    public MaintenanceTicket getTicketDetail(Long id) {
        MaintenanceTicket ticket = this.getById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.MAINTENANCE_NOT_EXIST);
        }
        return ticket;
    }

    @Override
    public MaintenanceTicket getByFaultReportId(Long faultReportId) {
        return this.getOne(new LambdaQueryWrapper<MaintenanceTicket>()
                .eq(MaintenanceTicket::getFaultReportId, faultReportId)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startMaintenance(Long id) {
        MaintenanceTicket ticket = this.getById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.MAINTENANCE_NOT_EXIST);
        }

        if (ticket.getStatus() != Constants.MAINTENANCE_STATUS_PENDING) {
            throw new BusinessException("只能开始待维修的工单");
        }

        ticket.setStatus(Constants.MAINTENANCE_STATUS_IN_PROGRESS);
        ticket.setStartTime(LocalDateTime.now());

        // 添加进度记录
        String newNote = LocalDateTime.now() + ": 开始维修";
        ticket.setProgressNotes(newNote);

        this.updateById(ticket);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeMaintenance(Long id, MaintenanceTicketDTO dto) {
        MaintenanceTicket ticket = this.getById(id);
        if (ticket == null) {
            throw new BusinessException(ResultCode.MAINTENANCE_NOT_EXIST);
        }

        if (ticket.getStatus() == Constants.MAINTENANCE_STATUS_COMPLETED) {
            throw new BusinessException(ResultCode.MAINTENANCE_ALREADY_COMPLETED);
        }

        ticket.setStatus(Constants.MAINTENANCE_STATUS_COMPLETED);
        ticket.setCompleteTime(LocalDateTime.now());

        // 计算实际维修天数
        if (ticket.getStartTime() != null) {
            long days = ChronoUnit.DAYS.between(ticket.getStartTime().toLocalDate(),
                    LocalDateTime.now().toLocalDate()) + 1;
            ticket.setActualDays((int) days);
        }

        if (dto != null) {
            if (dto.getActualCost() != null) {
                ticket.setActualCost(dto.getActualCost());
            }
        }

        // 添加进度记录
        String currentNotes = ticket.getProgressNotes();
        String newNote = LocalDateTime.now() + ": 维修完成";
        if (StringUtils.hasText(currentNotes)) {
            ticket.setProgressNotes(currentNotes + "\n" + newNote);
        } else {
            ticket.setProgressNotes(newNote);
        }

        this.updateById(ticket);

        // 将无人机状态恢复为在售
        droneService.updateStatus(ticket.getDroneId(), Constants.DRONE_STATUS_AVAILABLE);
    }

    /**
     * 生成工单号
     */
    private String generateTicketNo() {
        return "MNT" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }
}
