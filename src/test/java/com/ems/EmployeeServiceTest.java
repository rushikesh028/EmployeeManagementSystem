package com.ems;

import com.ems.dao.DepartmentDAO;
import com.ems.dao.EmployeeDAO;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.model.Employee;
import com.ems.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeDAO employeeDAO;

    @Mock
    private DepartmentDAO departmentDAO;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;

    @BeforeEach
    void setup() {
        sampleEmployee = Employee.builder()
                .id(1L)
                .firstName("Amit")
                .lastName("Sharma")
                .email("amit.sharma@test.com")
                .salary(new BigDecimal("75000"))
                .status(Employee.EmployeeStatus.ACTIVE)
                .departmentId(1L)
                .build();
    }

    @Test
    void createEmployee_Success() {
        when(employeeDAO.existsByEmail(any())).thenReturn(false);
        when(departmentDAO.existsById(any())).thenReturn(true);
        when(employeeDAO.save(any())).thenReturn(sampleEmployee);

        Employee result = employeeService.createEmployee(sampleEmployee);

        assertNotNull(result);
        assertEquals("Amit", result.getFirstName());
        verify(employeeDAO, times(1)).save(any(Employee.class));
    }

    @Test
    void createEmployee_DuplicateEmail_ThrowsException() {
        when(employeeDAO.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> employeeService.createEmployee(sampleEmployee));

        verify(employeeDAO, never()).save(any());
    }

    @Test
    void getEmployeeById_NotFound_ThrowsException() {
        when(employeeDAO.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(99L));
    }

    @Test
    void deleteEmployee_NotFound_ThrowsException() {
        when(employeeDAO.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(99L));

        verify(employeeDAO, never()).deleteById(any());
    }

    @Test
    void updateStatus_Success() {
        when(employeeDAO.existsById(1L)).thenReturn(true);
        when(employeeDAO.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        Employee result = employeeService.updateEmployeeStatus(1L, Employee.EmployeeStatus.INACTIVE);

        assertNotNull(result);
        verify(employeeDAO).updateStatus(1L, Employee.EmployeeStatus.INACTIVE);
    }
}
