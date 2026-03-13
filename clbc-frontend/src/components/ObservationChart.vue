<script setup>
import { computed } from 'vue';
import { Line } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  CategoryScale,
  Filler
} from 'chart.js';

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  CategoryScale,
  Filler
);

const props = defineProps({
  label: {
    type: String,
    required: true
  },
  observations: {
    type: Array,
    required: true
  },
  color: {
    type: String,
    default: '#6366f1' // primary indigo
  }
});

const chartData = computed(() => {
  return {
    labels: props.observations.map(obs => {
      const date = new Date(obs.observedAt);
      return date.toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    }),
    datasets: [
      {
        label: props.label,
        data: props.observations.map(obs => parseFloat(obs.value)),
        borderColor: props.color,
        backgroundColor: props.color + '20', // Add transparency for filler
        fill: true,
        tension: 0.4,
        pointBackgroundColor: props.color,
        pointBorderColor: '#fff',
        pointHoverRadius: 6,
        pointRadius: 4
      }
    ]
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      mode: 'index',
      intersect: false,
      backgroundColor: '#1e293b',
      padding: 12,
      titleFont: { size: 14, weight: 'bold' },
      bodyFont: { size: 13 },
      cornerRadius: 8,
      displayColors: false
    }
  },
  scales: {
    y: {
      beginAtZero: false,
      grid: {
        color: 'rgba(0, 0, 0, 0.05)',
        drawBorder: false
      },
      ticks: {
        font: { size: 12 },
        color: '#64748b'
      }
    },
    x: {
      grid: {
        display: false
      },
      ticks: {
        font: { size: 11 },
        color: '#64748b',
        maxRotation: 45,
        minRotation: 45
      }
    }
  },
  interaction: {
    intersect: false,
    mode: 'nearest'
  }
};
</script>

<template>
  <div class="chart-container">
    <Line :data="chartData" :options="chartOptions" />
  </div>
</template>

<style scoped>
.chart-container {
  height: 300px;
  width: 100%;
}
</style>
