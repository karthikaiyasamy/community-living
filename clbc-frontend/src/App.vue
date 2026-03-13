<script setup>
import { onMounted, onUnmounted, watch } from 'vue';
import { useAuthStore } from './store/auth';
import { useNotificationStore } from './store/notification';
import GlobalToast from './components/GlobalToast.vue';

const authStore = useAuthStore();
const notificationStore = useNotificationStore();

// Idle timeout management (30 minutes)
const IDLE_TIMEOUT = 30 * 60 * 1000;
let idleTimer = null;

const resetIdleTimer = () => {
  if (idleTimer) clearTimeout(idleTimer);
  
  if (authStore.isAuthenticated) {
    idleTimer = setTimeout(() => {
      authStore.logout('/login?reason=expired');
    }, IDLE_TIMEOUT);
  }
};

const activityEvents = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart'];

onMounted(() => {
  activityEvents.forEach(event => {
    window.addEventListener(event, resetIdleTimer);
  });
  
  // Initial check if authenticated
  if (authStore.isAuthenticated) {
    resetIdleTimer();
  }
});

onUnmounted(() => {
  activityEvents.forEach(event => {
    window.removeEventListener(event, resetIdleTimer);
  });
  if (idleTimer) clearTimeout(idleTimer);
});

// Watch for authentication changes to start/stop timer
watch(() => authStore.isAuthenticated, (isAuth) => {
  if (isAuth) {
    resetIdleTimer();
  } else {
    if (idleTimer) clearTimeout(idleTimer);
  }
});
</script>

<template>
  <router-view />
  <GlobalToast />
</template>

<style>
/* Global styles are handled in style.css */
:root {
  --warning: #f59e0b;
  --warning-light: #fef3c7;
}
</style>
