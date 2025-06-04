<script setup lang="ts">
const props = defineProps<{
  feedback: Feedback;
}>();
</script>

<template>
  <div class="mb-2 flex justify-between items-center">
    <span class="text-sm text-gray-500">
      {{ props.feedback.sender.firstName }} {{ props.feedback.sender.infix ? props.feedback.sender.infix + ' ' : '' }}{{ props.feedback.sender.lastName }}
    </span>
    <span class="text-sm text-gray-500">
      {{ props.feedback.formName }}
    </span>
    <span class="text-sm text-gray-500">
      {{ new Date(props.feedback.date).toLocaleDateString('nl-NL', { day: '2-digit', month: '2-digit', year: 'numeric' }) }}
    </span>
  </div>

  <!-- Questions and Answers -->
  <div v-if="props.feedback.answers && props.feedback.answers.length" class="space-y-4 text-left">
    <div
        v-for="answer in props.feedback.answers"
        :key="answer.id"
        class="p-4 bg-white border rounded-md shadow-sm"
    >
      <p class="text-gray-700 font-medium">
        {{ answer.question }}
      </p>
      <p class="text-gray-600 mt-2">
        {{ answer.content || 'Geen antwoord gegeven' }}
      </p>
    </div>
  </div>

  <!-- Score -->
  <div class="mt-4">
    <p class="text-gray-700">
      Beoordeling: <span class="font-bold">{{ props.feedback.score }}</span>/5
    </p>
  </div>
</template>

<style scoped>

</style>