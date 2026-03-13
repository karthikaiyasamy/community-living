<script setup>
import { useNotificationStore } from '../store/notification';

const store = useNotificationStore();
</script>

<template>
  <Transition name="toast-slide">
    <div v-if="store.show" class="global-toast" :class="store.type">
      <div class="toast-indicator"></div>
      <div class="toast-icon">
        <svg v-if="store.type === 'success'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg>
        <svg v-else-if="store.type === 'error'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>
        <svg v-else-if="store.type === 'warning'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><path d="M12 9v4"/><path d="M12 17h.01"/></svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg>
      </div>
      <div class="toast-message">
        {{ store.message }}
      </div>
      <button class="toast-close" @click="store.clear">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
      </button>
    </div>
  </Transition>
</template>

<style scoped>
.global-toast {
  position: fixed;
  bottom: 32px;
  right: 32px;
  min-width: 320px;
  max-width: 480px;
  background: white;
  border-radius: var(--radius-md);
  box-shadow: 0 12px 24px -6px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 12px;
  z-index: 9999;
  overflow: hidden;
}

.toast-indicator {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.success .toast-indicator { background: var(--success); }
.error .toast-indicator { background: var(--danger); }
.warning .toast-indicator { background: var(--warning); }
.info .toast-indicator { background: var(--primary); }

.toast-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success .toast-icon { background: #ecfdf5; color: var(--success); }
.error .toast-icon { background: #fef2f2; color: var(--danger); }
.warning .toast-icon { background: #fffbeb; color: var(--warning); }
.info .toast-icon { background: #f0f9ff; color: var(--primary); }

.toast-message {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-main);
  line-height: 1.4;
}

.toast-close {
  flex-shrink: 0;
  padding: 4px;
  color: var(--text-muted);
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.toast-close:hover {
  background: #f1f5f9;
  color: var(--text-main);
}

/* Animations */
.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
}

.toast-slide-enter-from {
  transform: translateX(100%) scale(0.9);
  opacity: 0;
}

.toast-slide-leave-to {
  transform: translateY(20px) scale(0.95);
  opacity: 0;
}
</style>
