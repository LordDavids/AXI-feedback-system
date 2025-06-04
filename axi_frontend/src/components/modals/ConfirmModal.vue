<script setup lang="ts">
import {VueFinalModal} from "vue-final-modal";
import {computed} from "vue";

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'success'
export type IconVariant = 'danger' | 'success' | 'none'

export type ModalButton = {
  text: string
  variant: ButtonVariant
  action: () => void
}

const props = defineProps<{
  title: string
  description: string
  buttons: ModalButton[]
  icon: IconVariant
}>()
</script>

<template>
  <VueFinalModal
      class="flex justify-center items-center"
      content-class="flex flex-col max-w-xl p-4 bg-white border rounded-xl space-y-2 text-center"
  >
    <div class="p-2">
      <i :class="'i-' + props.icon" class="rounded-xl p-7"/>
      <h1 class="text-4xl font-bold mb-2">
        {{ title }}
      </h1>
      
      <p class="mt-3">
        {{ description }}
      </p>
    </div>

    <div class="flex flex-col space-y-4 py-4 px-4">
      <template v-for="(button, index) in buttons" :key="index">
        <button
            class="btn"
            :class="button.variant"
            @click="button.action"
        >
          {{ button.text }}
        </button>
      </template>
    </div>
  </VueFinalModal>
</template>

<style scoped>
.primary {
  background-color: #7B42FA;
  color: white;
}
.primary:hover {
  background-color: #733dea;
}

.secondary {
  @apply text-black bg-transparent border border-gray-300;
}
.secondary:hover {
  @apply bg-gray-50;
}

.danger {
  background-color: #e3342f;
  color: white;
}
.danger:hover {
  background-color: #d23934;
}

.success {
  background-color: #38c172;
  color: white;
}
.success:hover {
  background-color: #2f855a;
}

i {
  width: 24px;
  height: 24px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  display: inline-block;
}

.i-danger {
  background-color: #FEE2E2;
  background-image: url('data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="-3 -3 30 30" stroke-width="1.5" stroke="%23EF4444" %3E%3Cpath stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z"%3E%3C/path%3E%3C/svg%3E');
}

.i-success {
  background-color: #DCFCE7;
  background-image: url('data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="-3 -3 30 30" stroke-width="1.5" stroke="%2322C55E" %3E%3Cpath stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"%3E%3C/path%3E%3C/svg%3E');
}

.i-none{
  display: none;
}
</style>