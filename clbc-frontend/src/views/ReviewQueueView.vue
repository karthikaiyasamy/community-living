<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';
import { useNotificationStore } from '../store/notification';

const notificationStore = useNotificationStore();

const residents = ref([]);
const loading = ref(true);

const fetchIncomingResidents = async () => {
  try {
    const response = await api.get('/residents', { params: { status: 'RECEIVED' } });
    residents.value = response.data.content;
  } catch (error) {
    console.error('Error fetching review queue:', error);
  } finally {
    loading.value = false;
  }
};

const activateResident = async (id) => {
  if (!confirm('Are you sure you want to activate this resident after review?')) return;
  
  try {
    await api.post(`/residents/${id}/activate`);
    residents.value = residents.value.filter(r => r.id !== id);
    notificationStore.success('Resident clinical profile activated');
  } catch (error) {
    console.error('Error activating resident:', error);
    notificationStore.error('Failed to activate resident. Please check clinical records.');
  }
};

onMounted(() => {
  fetchIncomingResidents();
});
</script>

<template>
  <div class="page-header">
    <div>
      <h1>Review Queue</h1>
      <p>Verify and activate residents imported from external clinical systems</p>
    </div>
  </div>

  <div v-if="loading" class="loading-state">
    <div class="loader"></div>
    <p>Loading incoming data...</p>
  </div>

  <div v-else-if="residents.length === 0" class="empty-queue card animate-fade-in">
    <div class="empty-icon">
       <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
    </div>
    <h2>Review Queue Empty</h2>
    <p>All incoming resident data has been processed and activated.</p>
  </div>

  <div v-else class="review-grid animate-fade-in">
    <div v-for="resident in residents" :key="resident.id" class="card review-card">
      <div class="card-badges">
        <span class="badge badge-received">RECEIVED</span>
        <span class="badge badge-system">FHIR</span>
      </div>
      
      <div class="resident-info">
        <div class="avatar-sm">{{ resident.firstName[0] }}{{ resident.lastName[0] }}</div>
        <div>
          <h3>{{ resident.firstName }} {{ resident.lastName }}</h3>
          <p>MRN: {{ resident.mrn || 'N/A' }}</p>
        </div>
      </div>

      <div class="review-details">
         <div class="detail-item">
           <span class="label">Date of Birth</span>
           <span class="value">{{ resident.dateOfBirth }}</span>
         </div>
         <div class="detail-item">
           <span class="label">Gender</span>
           <span class="value">{{ resident.gender }}</span>
         </div>
      </div>

      <div class="card-footer">
        <router-link :to="{ name: 'ResidentDetail', params: { id: resident.id } }" class="btn btn-outline">
          Review Data
        </router-link>
        <button @click="activateResident(resident.id)" class="btn btn-primary">
          Activate
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.page-header p {
  color: var(--text-muted);
}

.empty-queue {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  text-align: center;
  border-style: dashed;
  background: transparent;
}

.empty-icon {
  margin-bottom: 20px;
  color: var(--secondary);
}

.empty-queue h2 {
  margin-bottom: 8px;
  color: var(--text-main);
}

.empty-queue p {
  color: var(--text-muted);
}

.review-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.review-card {
  display: flex;
  flex-direction: column;
  transition: all 0.2s ease;
}

.review-card:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow);
}

.card-badges {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.badge {
  font-size: 10px;
  font-weight: 700;
  padding: 4px 8px;
  border-radius: 4px;
}

.badge-received {
  background: #fef9c3;
  color: #854d0e;
}

.badge-system {
  background: #dcfce7;
  color: #166534;
}

.resident-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.avatar-sm {
  width: 48px;
  height: 48px;
  background: var(--primary-light);
  color: var(--primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
}

.resident-info h3 {
  font-size: 18px;
  margin-bottom: 2px;
}

.resident-info p {
  font-size: 13px;
  color: var(--text-muted);
}

.review-details {
  background: var(--bg-main);
  padding: 16px;
  border-radius: var(--radius-md);
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.label {
  font-size: 12px;
  color: var(--text-muted);
}

.value {
  font-size: 13px;
  font-weight: 600;
}

.card-footer {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.btn-outline {
  border: 1px solid var(--border);
  color: var(--text-main);
  font-size: 14px;
}

.btn-outline:hover {
  background: var(--bg-main);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}
</style>
