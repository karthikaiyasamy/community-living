<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import api from '../services/api';

const alerts = ref([]);
const isOpen = ref(false);
const loading = ref(false);

const fetchAlerts = async () => {
  try {
    const response = await api.get('/alerts');
    alerts.value = response.data;
  } catch (error) {
    console.error('Failed to fetch alerts:', error);
  }
};

const resolveAlert = async (id) => {
  try {
    await api.post(`/alerts/${id}/resolve`);
    alerts.value = alerts.value.filter(a => a.id !== id);
  } catch (error) {
    console.error('Failed to resolve alert:', error);
  }
};

let pollInterval;
onMounted(() => {
  fetchAlerts();
  pollInterval = setInterval(fetchAlerts, 30000); // Poll every 30s
});

onUnmounted(() => {
  clearInterval(pollInterval);
});

const togglePanel = () => {
  isOpen.value = !isOpen.value;
  if (isOpen.value) fetchAlerts();
};
</script>

<template>
  <div class="alert-center-container">
    <button @click="togglePanel" class="icon-btn relative">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
      </svg>
      <span v-if="alerts.length > 0" class="badge-dot"></span>
    </button>

    <Transition name="fade-scale">
      <div v-if="isOpen" class="alert-dropdown card">
        <div class="dropdown-header">
          <h3>Clinical Alerts</h3>
          <span class="count-badge" v-if="alerts.length">{{ alerts.length }} Active</span>
        </div>

        <div class="alert-list custom-scrollbar">
          <div v-for="alert in alerts" :key="alert.id" class="alert-item" :class="alert.severity.toLowerCase()">
            <div class="alert-icon">
              <svg v-if="alert.severity === 'CRITICAL'" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
            </div>
            <div class="alert-body">
              <div class="alert-meta">
                <span class="resident-name">{{ alert.residentName }}</span>
                <span class="alert-time">{{ new Date(alert.createdAt).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) }}</span>
              </div>
              <p class="alert-msg">{{ alert.message }}</p>
              <div class="alert-actions">
                <button @click="resolveAlert(alert.id)" class="btn-text">Resolve</button>
                <router-link :to="{ name: 'ResidentDetail', params: { id: alert.residentId } }" @click="isOpen = false" class="btn-text">View Patient</router-link>
              </div>
            </div>
          </div>
          
          <div v-if="alerts.length === 0" class="empty-alerts">
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="m12 6.5 7 7-7 7"/><path d="M5 12.5h14"/><path d="M12 2.5v4"/></svg>
            <p>No active clinical alerts</p>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.alert-center-container {
  position: relative;
}

.relative { position: relative; }

.badge-dot {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 10px;
  height: 10px;
  background: var(--danger);
  border: 2px solid white;
  border-radius: 50%;
}

.alert-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  right: -10px;
  width: 360px;
  max-height: 500px;
  padding: 0;
  display: flex;
  flex-direction: column;
  z-index: 1000;
  box-shadow: var(--shadow-xl);
  border: 1px solid var(--border);
}

.dropdown-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--bg-main);
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.dropdown-header h3 {
  font-size: 15px;
  font-weight: 700;
  margin: 0;
}

.count-badge {
  background: var(--danger-light);
  color: var(--danger);
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 10px;
}

.alert-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.alert-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: var(--radius-md);
  margin-bottom: 8px;
  transition: background 0.2s;
}

.alert-item.warning { background: #fffcf0; }
.alert-item.critical { background: #fff5f5; }

.alert-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.warning .alert-icon { color: #d97706; }
.critical .alert-icon { color: #dc2626; }

.alert-body {
  flex: 1;
  min-width: 0;
}

.alert-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.resident-name {
  font-weight: 700;
  font-size: 13px;
  color: var(--text-main);
}

.alert-time {
  font-size: 11px;
  color: var(--text-muted);
}

.alert-msg {
  font-size: 13px;
  color: var(--text-main);
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.alert-actions {
  display: flex;
  gap: 16px;
}

.btn-text {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary);
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  text-decoration: none;
}

.btn-text:hover {
  text-decoration: underline;
}

.empty-alerts {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-muted);
}

.empty-alerts p {
  margin-top: 12px;
  font-size: 14px;
}

.fade-scale-enter-active, .fade-scale-leave-active {
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.fade-scale-enter-from, .fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.95) translateY(-10px);
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
</style>
