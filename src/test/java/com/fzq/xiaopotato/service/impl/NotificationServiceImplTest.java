package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.mapper.NotificationMapper;
import com.fzq.xiaopotato.model.entity.Notification;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private NotificationMapper notificationMapper;

    private NotificationVO mockNotificationVO;

    @BeforeEach
    void setUp() {
        mockNotificationVO = new NotificationVO();
        mockNotificationVO.setSourceId(1L);
        mockNotificationVO.setFirstName("John");
        mockNotificationVO.setLastName("Doe");
        mockNotificationVO.setAccount("john_doe");
        mockNotificationVO.setAvatar("avatar_url");
        mockNotificationVO.setNotificationType("COMMENT");
        mockNotificationVO.setTimestamp("2024-11-25 12:34:56");
    }

    @Test
    void testStoreNotification_Success() {
        // Arrange
        Notification mockNotification = new Notification();
        BeanUtils.copyProperties(mockNotificationVO, mockNotification);
        mockNotification.setUserId(2L);
        mockNotification.setIsRead(0);

        when(notificationMapper.insert(any(Notification.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> notificationService.storeNotification(2L, mockNotificationVO));

        // Assert
        verify(notificationMapper, times(1)).insert(any(Notification.class));
    }

    @Test
    void testStoreNotification_Failure() {
        // Arrange
        when(notificationMapper.insert(any(Notification.class))).thenReturn(0);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> notificationService.storeNotification(2L, mockNotificationVO));
        assertEquals("Failed to store notification", exception.getMessage());
        verify(notificationMapper, times(1)).insert(any(Notification.class));
    }

    @Test
    void testGetUnreadNotifications_Success() {
        // Arrange
        Notification mockNotification = new Notification();
        BeanUtils.copyProperties(mockNotificationVO, mockNotification);
        mockNotification.setUserId(1L);
        mockNotification.setIsRead(0);

        when(notificationMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(mockNotification));

        // Act
        List<NotificationVO> result = notificationService.getUnreadNotifications(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockNotificationVO.getFirstName(), result.get(0).getFirstName());
        verify(notificationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetUnreadNotifications_Empty() {
        // Arrange
        when(notificationMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Act
        List<NotificationVO> result = notificationService.getUnreadNotifications(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testMarkNotificationsAsRead_Success() {
        // Arrange
        when(notificationMapper.update(any(Notification.class), any(QueryWrapper.class))).thenReturn(5);

        // Act
        assertDoesNotThrow(() -> notificationService.markNotificationsAsRead(1L));

        // Assert
        verify(notificationMapper, times(1)).update(any(Notification.class), any(QueryWrapper.class));
    }

    @Test
    void testGetAllNotifications_Success() {
        // Arrange
        Notification mockNotification1 = new Notification();
        mockNotification1.setUserId(1L);
        mockNotification1.setIsRead(0);

        Notification mockNotification2 = new Notification();
        mockNotification2.setUserId(1L);
        mockNotification2.setIsRead(1);

        when(notificationMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(mockNotification1, mockNotification2));

        // Act
        List<NotificationVO> result = notificationService.getAllNotifications(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    void testGetAllNotifications_Empty() {
        // Arrange
        when(notificationMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // Act
        List<NotificationVO> result = notificationService.getAllNotifications(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationMapper, times(1)).selectList(any(QueryWrapper.class));
    }
}
