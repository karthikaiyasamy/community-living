<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';
import { useNotificationStore } from '../store/notification';

const notificationStore = useNotificationStore();

const residents = ref([]);
const prescriptions = ref([]);
const administrations = ref([]);
const loading = ref(true);
const recording = ref(false);

const fetchData = async () => {
  try {
    const today = new Date().toISOString().split('T')[0];
    const [resResp, prescResp, adminResp] = await Promise.all([
      api.get('/residents', { params: { status: 'ACTIVE' } }),
      // We need a way to get all active prescriptions for the community
      // I added a repository method, let's assume there's an endpoint or we filter
      api.get('/residents'), // Temporary: we'll get residents and their prescriptions
      api.get('/mar/daily', { params: { date: today } })
    ]);

    residents.value = resResp.data.content;
    administrations.value = adminResp.data;

    // Fetch prescriptions for each resident
    const prescPromises = residents.value.map(r => 
      api.get(`/residents/${r.id}/prescriptions`)
        .then(resp => ({ residentId: r.id, data: resp.data.filter(p => p.status === 'ACTIVE') }))
    );
    
    const prescResults = await Promise.all(prescPromises);
    prescriptions.value = prescResults;

  } catch (error) {
    console.error('Error fetching MAR data:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchData();
});

const isDoseTakenToday = (prescriptionId) => {
  return administrations.value.some(a => a.prescriptionId === prescriptionId && a.doseTaken);
};

const recordDose = async (residentId, prescriptionId, taken) => {
  recording.value = true;
  try {
    const now = new Date();
    const payload = {
      dateAdministered: now.toISOString().split('T')[0],
      timeAdministered: now.toTimeString().split(' ')[0],
      doseTaken: taken,
      notes: taken ? 'Dose signed off in Shift MAR' : 'Dose marked as missed/refused'
    };
    
    const response = await api.post(`/prescriptions/${prescriptionId}/administrations`, payload);
    administrations.value.push(response.data);
    notificationStore.success('Medication administration recorded');
  } catch (error) {
    console.error('Failed to record dose:', error);
    notificationStore.error('Failed to record medication dose');
  } finally {
    recording.value = false;
  }
};

const getResidentPrescriptions = (residentId) => {
  const entry = prescriptions.value.find(p => p.residentId === residentId);
  return entry ? entry.data : [];
};

const progressStats = computed(() => {
  const total = prescriptions.value.reduce((acc, curr) => acc + curr.data.length, 0);
  const completed = administrations.value.filter(a => a.doseTaken).length;
  return { total, completed, percent: total > 0 ? Math.round((completed / total) * 100) : 0 };
});
</script>

<template>
  <div class="page-header">
    <div>
      <h1>Shift MAR</h1>
      <p>Daily Medication Administration Record for all residents</p>
    </div>
    <div class="header-stats card" v-if="!loading">
       <div class="stat-item">
          <span class="stat-label">Shift Progress</span>
          <span class="stat-value">{{ progressStats.completed }} / {{ progressStats.total }}</span>
       </div>
       <div class="progress-bar-bg">
          <div class="progress-bar-fill" :style="{ width: progressStats.percent + '%' }"></div>
       </div>
    </div>
  </div>

  <div v-if="loading" class="loading-state">
    <div class="loader"></div>
    <p>Preparing shift records...</p>
  </div>

  <div v-else class="mar-workspace animate-fade-in">
    <div v-for="resident in residents" :key="resident.id" class="card resident-mar-section">
      <div class="resident-header">
         <div class="avatar-sm">{{ resident.firstName[0] }}{{ resident.lastName[0] }}</div>
         <div class="resident-info">
            <h3>{{ resident.firstName }} {{ resident.lastName }}</h3>
            <p>MRN: {{ resident.mrn || 'N/A' }}</p>
         </div>
         <router-link :to="{ name: 'ResidentDetail', params: { id: resident.id } }" class="btn btn-outline btn-sm">
            View Profile
         </router-link>
      </div>

      <div class="med-list">
         <div v-for="presc in getResidentPrescriptions(resident.id)" :key="presc.id" class="med-row">
            <div class="med-details">
               <span class="med-name">{{ presc.medicationName }}</span>
               <span class="med-meta">{{ presc.dosage }} • {{ presc.frequency }}</span>
            </div>
            
            <div class="med-status">
               <div v-if="isDoseTakenToday(presc.id)" class="status-completed">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" class="text-success"><polyline points="20 6 9 17 4 12"/></svg>
                  <span class="text-success font-bold">TAKEN</span>
               </div>
               <div v-else class="status-actions">
                  <button @click="recordDose(resident.id, presc.id, true)" class="btn btn-success btn-sm" :disabled="recording">
                     Taken
                  </button>
                  <button @click="recordDose(resident.id, presc.id, false)" class="btn btn-outline btn-sm" :disabled="recording">
                     Skip
                  </button>
               </div>
            </div>
         </div>
         <div v-if="getResidentPrescriptions(resident.id).length === 0" class="empty-meds">
            No active medications for this resident.
         </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-stats {
  padding: 12px 20px;
  min-width: 250px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 600;
}

.stat-value {
  font-weight: 700;
  color: var(--primary);
}

.progress-bar-bg {
  height: 8px;
  background: var(--bg-main);
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: var(--primary);
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.resident-mar-section {
  margin-bottom: 24px;
  overflow: hidden;
}

.resident-header {
  padding: 20px;
  background: var(--bg-main);
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid var(--border);
}

.avatar-sm {
  width: 40px;
  height: 40px;
  background: var(--primary-light);
  color: var(--primary);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.resident-info h3 {
  font-size: 16px;
  margin-bottom: 2px;
}

.resident-info p {
  font-size: 12px;
  color: var(--text-muted);
}

.med-list {
  padding: 8px 20px;
}

.med-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--border);
}

.med-row:last-child {
  border-bottom: none;
}

.med-details {
  display: flex;
  flex-direction: column;
}

.med-name {
  font-weight: 700;
  font-size: 15px;
}

.med-meta {
  font-size: 12px;
  color: var(--text-muted);
}

.status-completed {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-right: 12px;
}

.status-actions {
  display: flex;
  gap: 8px;
}

.btn-success {
  background: #10b981;
  color: white;
  border: none;
}

.btn-success:hover {
  background: #059669;
}

.empty-meds {
  padding: 20px 0;
  text-align: center;
  color: var(--text-muted);
  font-size: 14px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
}

.text-success { color: #10b981; }
.text-danger { color: #ef4444; }
.font-bold { font-weight: 700; }
</style>
