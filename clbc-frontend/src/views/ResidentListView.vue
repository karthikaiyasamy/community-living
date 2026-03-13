<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const residents = ref([]);
const loading = ref(true);

const fetchResidents = async () => {
  try {
    const response = await api.get('/residents');
    residents.value = response.data.content;
  } catch (error) {
    console.error('Error fetching residents:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchResidents();
});
</script>

<template>
  <div class="page-header">
    <div>
      <h1>Residents</h1>
      <p>Manage community members and their care plans</p>
    </div>
    <button class="btn btn-primary">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M12 5v14"/></svg>
      Add Resident
    </button>
  </div>

  <div v-if="loading" class="loading-state">
    <div class="loader"></div>
    <p>Loading residents...</p>
  </div>

  <div v-else class="resident-grid animate-fade-in">
    <div v-for="resident in residents" :key="resident.id" class="card resident-card">
      <div class="card-header">
        <div class="avatar-large">{{ resident.firstName[0] }}{{ resident.lastName[0] }}</div>
        <div class="status-badge" :class="resident.status.toLowerCase()">{{ resident.status }}</div>
      </div>
      <div class="card-content">
        <h3>{{ resident.firstName }} {{ resident.lastName }}</h3>
        <p class="resident-meta">{{ resident.gender }} • {{ resident.dateOfBirth }}</p>
        
        <div class="quick-stats">
          <div class="stat">
            <span class="stat-label">Room</span>
            <span class="stat-value">204A</span>
          </div>
          <div class="stat">
            <span class="stat-label">Care Level</span>
            <span class="stat-value">Medium</span>
          </div>
        </div>
      </div>
      <div class="card-footer">
        <router-link :to="{ name: 'ResidentDetail', params: { id: resident.id } }" class="btn btn-view">
          View Profile
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 4px;
}

.page-header p {
  color: var(--text-muted);
}

.resident-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.resident-card {
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, border-color 0.2s ease;
}

.resident-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.avatar-large {
  width: 56px;
  height: 56px;
  background: var(--primary-light);
  color: var(--primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
}

.status-badge {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 4px 10px;
  border-radius: 20px;
}

.status-badge.active {
  background: #ecfdf5;
  color: #10b981;
}

.card-content h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 4px;
}

.resident-meta {
  color: var(--text-muted);
  font-size: 14px;
  margin-bottom: 16px;
}

.quick-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  background: var(--bg-main);
  padding: 12px;
  border-radius: var(--radius-md);
  margin-bottom: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 11px;
  color: var(--text-muted);
  text-transform: uppercase;
  font-weight: 600;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-main);
}

.card-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.btn-view {
  width: 100%;
  color: var(--primary);
  font-weight: 600;
  font-size: 14px;
  gap: 6px;
}

.btn-view:hover {
  background: var(--primary-light);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  color: var(--text-muted);
}

.loader {
  width: 40px;
  height: 40px;
  border: 4px solid var(--border);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
