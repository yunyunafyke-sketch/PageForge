package com.afyke.pageforge.business.staff.service.impl;

import com.afyke.pageforge.business.staff.converter.OutsourcedStaffConverter;
import com.afyke.pageforge.business.staff.entity.OutsourcedStaffEntity;
import com.afyke.pageforge.business.staff.mapper.OutsourcedStaffMapper;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffCreateRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffUpdateRequest;
import com.afyke.pageforge.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 外包人员服务单元测试。 */
@ExtendWith(MockitoExtension.class)
class OutsourcedStaffServiceImplTest {

    /** 外包人员 Mapper。 */
    @Mock
    private OutsourcedStaffMapper outsourcedStaffMapper;

    /** 外包人员对象转换器。 */
    @Mock
    private OutsourcedStaffConverter outsourcedStaffConverter;

    /** 被测试的外包人员服务。 */
    @InjectMocks
    private OutsourcedStaffServiceImpl outsourcedStaffService;

    /** 新增成功后应返回数据库生成的 ID。 */
    @Test
    void shouldCreateStaffAndReturnId() {
        OutsourcedStaffCreateRequest request = new OutsourcedStaffCreateRequest();
        OutsourcedStaffEntity entity = new OutsourcedStaffEntity();
        entity.setId(12L);
        when(outsourcedStaffConverter.toEntity(request)).thenReturn(entity);

        assertEquals(12L, outsourcedStaffService.create(request));
        verify(outsourcedStaffMapper).insert(entity);
    }

    /** 修改有效数据时应返回真实的更新结果。 */
    @Test
    void shouldUpdateExistingStaff() {
        OutsourcedStaffUpdateRequest request = new OutsourcedStaffUpdateRequest();
        request.setId(8L);
        OutsourcedStaffEntity entity = new OutsourcedStaffEntity();
        when(outsourcedStaffMapper.selectById(8L)).thenReturn(entity);
        when(outsourcedStaffMapper.updateById(entity)).thenReturn(1);

        assertTrue(outsourcedStaffService.update(request));
        verify(outsourcedStaffConverter).updateEntity(request, entity);
    }

    /** 删除不存在的数据时应返回明确的业务错误。 */
    @Test
    void shouldRejectDeletingMissingStaff() {
        when(outsourcedStaffMapper.selectById(99L)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> outsourcedStaffService.delete(99L));

        assertEquals("OUTSOURCED_STAFF_NOT_FOUND", exception.getErrorCode());
    }
}
