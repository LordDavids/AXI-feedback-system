<script setup lang="ts">
import {computed, ref} from 'vue';

const props = defineProps<{
  totalPages: number;
  currentPage: number;
}>()

// Emits
const emit = defineEmits<{
  (e: 'pageChange', payload: { page: number; }): void;
}>();

const nextPage = () => {
  if (props.currentPage !== props.totalPages) {
    emit('pageChange', {page: props.currentPage + 1});
  }
};

const previousPage = () => {
  if (props.currentPage !== 1) {
    emit('pageChange', {page: props.currentPage - 1});
  }
};

const goToPage = (pageNumber: number) => {
  emit('pageChange', {page: pageNumber});
};

const goToFirstPage = () => {
  emit('pageChange', {page: 1});
};

const goToLastPage = () => {
  emit('pageChange', {page: totalPages});
};

const onPage = (page: number) => {
  return computed(() => page === props.currentPage);
};
</script>

<template>
  <div class="flex items-center justify-between px-4 py-3 sm:px-6">
    <div class="flex flex-1 justify-between sm:hidden">
      <a href="#" @click="previousPage" :class="[{'text-gray-300': onPage(1).value, 'hover:bg-gray-50': !onPage(1).value}, 'relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700']">Previous</a>
      <a href="#" @click="nextPage" :class="[{'text-gray-300': onPage(totalPages).value, 'hover:bg-gray-50': !onPage(totalPages).value}, 'relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700']">Next</a>
    </div>
    <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
      <div class="mr-auto ml-auto">
        <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
          <a href="#" @click="previousPage" :class="[{'text-gray-300': onPage(1).value, 'hover:bg-gray-50 focus:z-20 focus:outline-offset-0': !onPage(1).value}, 'relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300']">
            <span class="sr-only">Previous</span>
            <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true" data-slot="icon">
              <path fill-rule="evenodd" d="M11.78 5.22a.75.75 0 0 1 0 1.06L8.06 10l3.72 3.72a.75.75 0 1 1-1.06 1.06l-4.25-4.25a.75.75 0 0 1 0-1.06l4.25-4.25a.75.75 0 0 1 1.06 0Z" clip-rule="evenodd" />
            </svg>
          </a>
<!--           Current: "z-10 bg-indigo-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600", Default: "text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0" -->
          <a v-for="n in Number(totalPages)" href="#" @click="goToPage(n)" :aria-current="[{'page': onPage(n)}]" :class="[{'text-white z-10 AXI-bg focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600': onPage(n).value, 'text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0': !onPage(n).value}, 'relative inline-flex items-center px-4 py-2 focus:z-20 text-sm font-semibold']">{{ n }}</a>
          
          <a href="#" @click="nextPage" :class="[{'text-gray-300': onPage(totalPages).value, 'hover:bg-gray-50 focus:z-20 focus:outline-offset-0': !onPage(totalPages).value}, 'relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300']">
            <span class="sr-only">Next</span>
            <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true" data-slot="icon">
              <path fill-rule="evenodd" d="M8.22 5.22a.75.75 0 0 1 1.06 0l4.25 4.25a.75.75 0 0 1 0 1.06l-4.25 4.25a.75.75 0 0 1-1.06-1.06L11.94 10 8.22 6.28a.75.75 0 0 1 0-1.06Z" clip-rule="evenodd" />
            </svg>
          </a>
        </nav>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>