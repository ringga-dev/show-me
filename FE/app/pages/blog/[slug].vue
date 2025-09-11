<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 transition-colors duration-500 dark:from-slate-950 dark:to-slate-900">
    <!-- HERO dengan animasi dan efek visual yang lebih menarik -->
    <header v-if="hasCover" class="relative h-[75vh] w-full overflow-hidden">
      <img
          :src="post.coverImage"
          :alt="post.title || 'Blog cover'"
          class="absolute inset-0 h-full w-full object-cover transform scale-105 animate-pulse-slow"
      />
      <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent" />
      <div class="absolute inset-0 bg-gradient-to-r from-black/30 to-transparent" />
      <!-- Enhanced animated particles effect -->
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute -top-40 -right-40 h-80 w-80 rounded-full bg-purple-500/20 blur-3xl animate-pulse"></div>
        <div class="absolute -bottom-40 -left-40 h-80 w-80 rounded-full bg-blue-500/20 blur-3xl animate-pulse delay-1000"></div>
        <div class="absolute top-1/4 left-1/4 h-40 w-40 rounded-full bg-pink-500/10 blur-3xl animate-pulse delay-700"></div>
        <div class="absolute bottom-1/3 right-1/3 h-32 w-32 rounded-full bg-indigo-500/10 blur-3xl animate-pulse delay-1500"></div>
      </div>
      <!-- Animated geometric shapes -->
      <div class="absolute inset-0 overflow-hidden opacity-30">
        <div class="absolute top-20 left-10 w-16 h-16 border-l-2 border-t-2 border-white transform rotate-45 animate-float"></div>
        <div class="absolute bottom-32 right-20 w-12 h-12 border-r-2 border-b-2 border-white transform rotate-12 animate-float-reverse"></div>
        <div class="absolute top-1/3 right-1/4 w-20 h-20 border-l-2 border-b-2 border-white transform -rotate-12 animate-float-slow"></div>
      </div>
      <div class="pointer-events-none absolute inset-x-0 bottom-0 h-40 bg-gradient-to-t from-slate-50 dark:from-slate-950" />
      <div class="relative z-[1] mx-auto flex h-full max-w-6xl items-end px-4 pb-16 sm:px-6 lg:px-8">
        <div class="max-w-4xl space-y-8">
          <!-- Categories dengan animasi fade-in -->
          <div v-if="post.categories && post.categories.length > 0" class="flex flex-wrap gap-2 opacity-0 animate-fade-in-up animation-delay-200">
            <UBadge
                v-for="(category, index) in post.categories"
                :key="category"
                color="white"
                variant="soft"
                size="sm"
                class="bg-white/20 text-white border-white/30 backdrop-blur-sm hover:bg-white/30 transition-all duration-300 transform hover:scale-105 shadow-lg"
                :style="{ animationDelay: `${200 + index * 100}ms` }"
            >
              <UIcon name="i-heroicons-tag" class="w-3 h-3 mr-1" />
              {{ category }}
            </UBadge>
          </div>
          <!-- Title dengan animasi -->
          <h1 class="text-4xl md:text-6xl font-extrabold tracking-tight text-white leading-tight opacity-0 animate-fade-in-up animation-delay-300 drop-shadow-lg">
            {{ post.title || 'Untitled Post' }}
          </h1>
          <!-- Meta info dengan animasi -->
          <div class="flex flex-wrap items-center gap-x-6 gap-y-3 text-sm text-white/90 opacity-0 animate-fade-in-up animation-delay-400">
            <div class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm hover:bg-white/20 transition-all duration-300 shadow-md">
              <UIcon name="i-heroicons-calendar" class="h-4 w-4" />
              <time :datetime="post.createdAt">{{ formatDate(post.createdAt) }}</time>
            </div>
            <div class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm hover:bg-white/20 transition-all duration-300 shadow-md">
              <UIcon name="i-heroicons-clock" class="h-4 w-4" />
              <span>{{ readingTime }} min read</span>
            </div>
            <div v-if="post.viewCount !== undefined" class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm hover:bg-white/20 transition-all duration-300 shadow-md">
              <UIcon name="i-heroicons-eye" class="h-4 w-4" />
              <span>{{ formatNumber(post.viewCount) }} views</span>
            </div>
            <div class="flex items-center gap-2 px-3 py-1.5 rounded-full backdrop-blur-sm shadow-md hover:shadow-lg transition-all duration-300"
                 :class="post.published ? 'bg-green-500/20 text-green-300 hover:bg-green-500/30' : 'bg-amber-500/20 text-amber-300 hover:bg-amber-500/30'">
              <UIcon
                  :name="post.published ? 'i-heroicons-check-circle' : 'i-heroicons-lock-closed'"
                  class="h-4 w-4"
              />
              <span>{{ post.published ? 'Published' : 'Draft' }}</span>
            </div>
          </div>
        </div>
      </div>

    </header>

    <!-- Enhanced fallback banner saat tidak ada cover dengan desain yang lebih menarik -->
    <header v-else class="relative overflow-hidden bg-gradient-to-br from-indigo-600 via-purple-600 to-blue-600 py-24 dark:from-indigo-900 dark:via-purple-900 dark:to-blue-900">
      <!-- Enhanced animated background shapes -->
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute top-20 left-20 h-64 w-64 rounded-full bg-white/20 blur-3xl animate-pulse"></div>
        <div class="absolute bottom-20 right-20 h-64 w-64 rounded-full bg-white/20 blur-3xl animate-pulse delay-1000"></div>
        <div class="absolute top-1/3 right-1/4 h-48 w-48 rounded-full bg-pink-500/10 blur-3xl animate-pulse delay-700"></div>
        <div class="absolute bottom-1/3 left-1/4 h-40 w-40 rounded-full bg-indigo-500/10 blur-3xl animate-pulse delay-1500"></div>
      </div>
      <!-- Animated geometric shapes -->
      <div class="absolute inset-0 overflow-hidden opacity-20">
        <div class="absolute top-1/4 left-1/4 w-20 h-20 border-l-4 border-t-4 border-white transform rotate-45 animate-float"></div>
        <div class="absolute bottom-1/3 right-1/4 w-16 h-16 border-r-4 border-b-4 border-white transform rotate-12 animate-float-reverse"></div>
        <div class="absolute top-1/2 right-1/3 w-24 h-24 border-l-4 border-b-4 border-white transform -rotate-12 animate-float-slow"></div>
      </div>
      <div class="relative mx-auto max-w-5xl px-4 sm:px-6 lg:px-8 text-center">
        <div class="space-y-8">
          <h1 class="text-4xl md:text-5xl font-extrabold tracking-tight text-white leading-tight drop-shadow-lg">
            {{ post.title || 'Untitled Post' }}
          </h1>
          <div class="flex flex-wrap items-center justify-center gap-x-6 gap-y-3 text-sm text-white/90">
            <div class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm hover:bg-white/20 transition-all duration-300 shadow-md">
              <UIcon name="i-heroicons-calendar" class="h-4 w-4" />
              <time :datetime="post.createdAt">{{ formatDate(post.createdAt) }}</time>
            </div>
            <div class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm hover:bg-white/20 transition-all duration-300 shadow-md">
              <UIcon name="i-heroicons-clock" class="h-4 w-4" />
              <span>{{ readingTime }} min read</span>
            </div>
            <div v-if="post.viewCount !== undefined" class="flex items-center gap-2 bg-white/10 px-3 py-1.5 rounded-full backdrop-blur-sm hover:bg-white/20 transition-all duration-300 shadow-md">
              <UIcon name="i-heroicons-eye" class="h-4 w-4" />
              <span>{{ formatNumber(post.viewCount) }} views</span>
            </div>
          </div>
          <div v-if="post.categories && post.categories.length > 0" class="flex flex-wrap justify-center gap-2">
            <UBadge
                v-for="category in post.categories"
                :key="category"
                color="gray"
                variant="soft"
                size="sm"
                class="bg-white/20 text-white border-white/30 backdrop-blur-sm hover:bg-white/30 transition-all duration-300 transform hover:scale-105 shadow-lg"
            >
              <UIcon name="i-heroicons-tag" class="w-3 h-3 mr-1" />
              {{ category }}
            </UBadge>
          </div>
        </div>
      </div>
    </header>

    <!-- MAIN dengan desain yang lebih modern -->
    <main class="relative mx-auto max-w-5xl px-4 py-16 sm:px-6 md:py-24 lg:px-8">
      <!-- Enhanced background decoration -->
      <div class="absolute inset-0 -z-10 overflow-hidden">
        <div class="absolute top-20 right-0 h-96 w-96 rounded-full bg-purple-100/40 blur-3xl dark:bg-purple-900/30 animate-pulse-slow"></div>
        <div class="absolute bottom-20 left-0 h-96 w-96 rounded-full bg-blue-100/40 blur-3xl dark:bg-blue-900/30 animate-pulse-slow delay-1000"></div>
        <div class="absolute top-1/3 left-1/4 h-64 w-64 rounded-full bg-pink-100/30 blur-3xl dark:bg-pink-900/20 animate-pulse delay-700"></div>
        <div class="absolute bottom-1/3 right-1/4 h-64 w-64 rounded-full bg-indigo-100/30 blur-3xl dark:bg-indigo-900/20 animate-pulse delay-1500"></div>
      </div>

      <!-- Enhanced Breadcrumb dengan desain yang lebih menarik -->
      <nav class="mb-10 flex items-center space-x-2 text-xs text-slate-500 dark:text-slate-400">
        <NuxtLink to="/" class="flex items-center gap-1 hover:text-blue-600 dark:hover:text-blue-400 transition-colors duration-300 group">
          <UIcon name="i-heroicons-home" class="w-3 h-3 group-hover:scale-110 transition-transform" />
          <span class="font-medium">Home</span>
        </NuxtLink>
        <span class="text-slate-400">/</span>
        <NuxtLink to="/blog" class="flex items-center gap-1 hover:text-blue-600 dark:hover:text-blue-400 transition-colors duration-300 group">
          <UIcon name="i-heroicons-document-text" class="w-3 h-3 group-hover:scale-110 transition-transform" />
          <span class="font-medium">Blog</span>
        </NuxtLink>
        <span class="text-slate-400">/</span>
        <span class="truncate text-slate-900 dark:text-slate-100 font-medium">{{ post.title || 'Untitled Post' }}</span>
      </nav>

      <!-- Enhanced Tags dengan desain yang lebih menarik -->
      <div v-if="post.tags && post.tags.length > 0" class="mb-12">
        <h3 class="text-sm font-semibold text-slate-700 dark:text-slate-300 mb-4 flex items-center gap-2">
          <UIcon name="i-heroicons-hashtag" class="w-4 h-4 text-indigo-500 dark:text-indigo-400" />
          <span>Tags</span>
        </h3>
        <div class="flex flex-wrap gap-2">
          <UBadge
              v-for="(tag, index) in post.tags"
              :key="tag"
              color="gray"
              variant="outline"
              size="sm"
              class="hover:bg-slate-100 dark:hover:bg-slate-800 transition-all duration-300 transform hover:scale-105 hover:shadow-sm"
              :style="{ animationDelay: `${index * 50}ms` }"
          >
            #{{ tag }}
          </UBadge>
        </div>
      </div>

      <!-- Enhanced Loading State dengan animasi yang lebih menarik -->
      <div v-if="loading" class="flex justify-center items-center py-32">
        <div class="text-center space-y-6">
          <div class="relative">
            <UIcon name="i-heroicons-arrow-path" class="animate-spin h-16 w-16 text-indigo-600 mx-auto" />
            <div class="absolute inset-0 bg-indigo-500/20 rounded-full blur-xl animate-ping"></div>
          </div>
          <p class="text-slate-600 dark:text-slate-400 animate-pulse">Loading amazing content...</p>
        </div>
      </div>

      <!-- Enhanced Error State dengan desain yang lebih menarik -->
      <div v-else-if="error" class="text-center py-32">
        <div class="max-w-md mx-auto space-y-8">
          <div class="relative">
            <UIcon name="i-heroicons-exclamation-triangle" class="h-24 w-24 text-red-500 mx-auto" />
            <div class="absolute inset-0 bg-red-500/20 rounded-full blur-xl animate-pulse"></div>
          </div>
          <div>
            <h3 class="text-2xl font-bold text-slate-900 dark:text-white mb-3">Oops! Something went wrong</h3>
            <p class="text-slate-600 dark:text-slate-400">{{ error }}</p>
          </div>
          <UButton
              @click="fetchBlogPost"
              size="lg"
              icon="i-heroicons-arrow-path"
              class="mt-4 bg-gradient-to-r from-indigo-500 to-purple-600 hover:from-indigo-600 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-xl"
          >
            Try Again
          </UButton>
        </div>
      </div>

      <!-- CONTENT: tiga lapis fallback dengan desain yang lebih menarik -->
      <section v-else class="mx-auto max-w-none">
        <!-- Enhanced Skeleton saat loading awal -->
        <div v-if="pending" class="space-y-6">
          <div class="h-8 w-3/4 animate-pulse rounded-lg bg-slate-200 dark:bg-slate-800"></div>
          <div class="h-4 w-full animate-pulse rounded-lg bg-slate-200 dark:bg-slate-800"></div>
          <div class="h-4 w-5/6 animate-pulse rounded-lg bg-slate-200 dark:bg-slate-800"></div>
          <div class="h-64 w-full animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800"></div>
        </div>

        <!-- 1) Enhanced Render MDCRenderer jika berhasil parse -->
        <article
            v-else-if="parsedSafe.body"
            class="prose prose-lg prose-headings:font-semibold prose-h1:text-3xl prose-h2:text-2xl prose-h3:text-xl prose-h4:text-lg prose-p:text-base prose-p:leading-relaxed prose-a:text-blue-600 hover:prose-a:underline prose-code:text-slate-800 prose-pre:bg-slate-100 prose-pre:text-slate-800 prose-pre:rounded-lg prose-pre:p-4 prose-pre:shadow-md prose-blockquote:border-l-4 prose-blockquote:border-blue-500 prose-blockquote:text-slate-600 prose-blockquote:pl-4 prose-blockquote:italic prose-img:rounded-xl prose-img:shadow-lg prose-hr:border-slate-200 dark:prose-invert dark:prose-headings:text-white dark:prose-p:text-slate-300 dark:prose-a:text-blue-400 dark:prose-code:text-slate-200 dark:prose-pre:bg-slate-800 dark:prose-pre:text-slate-200 dark:prose-pre:shadow-lg dark:prose-blockquote:text-slate-400 dark:prose-hr:border-slate-700 mx-auto max-w-none leading-relaxed text-slate-800 dark:text-slate-200"
        >
          <MDCRenderer :body="parsedSafe.body" :data="parsedSafe.data" />
        </article>

        <!-- 2) Enhanced Fallback sederhana kalau parse gagal total: tampilkan konten mentah -->
        <article
            v-else
            class="prose prose-lg mx-auto max-w-none whitespace-pre-wrap rounded-2xl bg-white/90 p-10 text-slate-800 shadow-xl ring-1 ring-slate-200 dark:prose-invert dark:bg-slate-800/90 dark:text-slate-200 dark:ring-slate-700 backdrop-blur-sm"
        >
          {{ post.content }}
        </article>
      </section>

      <!-- Enhanced Share section dengan desain yang lebih menarik -->
      <footer v-if="!loading && !error" class="mt-24 pt-12 border-t border-slate-200 dark:border-slate-800">
        <div class="text-center space-y-10">
          <div>
            <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-3">Share this article</h2>
            <p class="text-slate-600 dark:text-slate-400">If you found this article helpful, share it with others</p>
          </div>

          <div class="flex justify-center gap-4">
            <!-- Enhanced Social sharing buttons -->
            <UButton
                @click="copyToClipboard"
                color="gray"
                variant="outline"
                size="lg"
                icon="i-heroicons-link"
                class="hover:bg-slate-100 dark:hover:bg-slate-800 transition-all duration-300 transform hover:-translate-y-0.5 hover:shadow-md"
            >
              {{ copied ? 'Copied!' : 'Copy Link' }}
            </UButton>
            <UButton
                @click="sharePost"
                color="blue"
                variant="outline"
                size="lg"
                icon="i-heroicons-share"
                class="hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-all duration-300 transform hover:-translate-y-0.5 hover:shadow-md"
            >
              Share
            </UButton>
          </div>

          <!-- Enhanced Related posts section -->
          <div class="mt-16 pt-10 border-t border-slate-200 dark:border-slate-800">
            <h3 class="text-xl font-semibold text-slate-900 dark:text-white mb-8 flex items-center justify-center gap-2">
              <UIcon name="i-heroicons-light-bulb" class="w-5 h-5 text-amber-500" />
              You might also like
            </h3>
            <div class="grid gap-8 md:grid-cols-2">
              <!-- Enhanced Placeholder for related posts -->
              <div v-for="(relatedPost, index) in relatedPosts" :key="index"
                   class="group bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-800 dark:to-slate-900 rounded-xl p-6 shadow-md hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1 border border-slate-200 dark:border-slate-700 cursor-pointer"
                   @click="navigateToPost(relatedPost.slug)">
                <div class="flex items-start gap-4">
                  <div class="flex-shrink-0">
                    <div class="w-16 h-16 rounded-lg bg-gradient-to-br from-indigo-100 to-purple-100 dark:from-indigo-900/50 dark:to-purple-900/50 flex items-center justify-center">
                      <UIcon name="i-heroicons-document-text" class="w-8 h-8 text-indigo-600 dark:text-indigo-400" />
                    </div>
                  </div>
                  <div class="flex-1">
                    <h4 class="font-semibold text-slate-900 dark:text-white group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors">
                      {{ relatedPost.title }}
                    </h4>
                    <p class="text-sm text-slate-600 dark:text-slate-400 mt-1 line-clamp-2">
                      {{ relatedPost.excerpt }}
                    </p>
                    <div class="flex items-center gap-2 mt-3 text-xs text-slate-500 dark:text-slate-400">
                      <UIcon name="i-heroicons-calendar" class="w-3 h-3" />
                      <span>{{ formatDate(relatedPost.createdAt) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Enhanced Newsletter subscription -->
          <div class="mt-16 bg-gradient-to-r from-indigo-50 to-purple-50 dark:from-indigo-900/20 dark:to-purple-900/20 rounded-2xl p-8 border border-indigo-100 dark:border-indigo-900/30">
            <div class="max-w-md mx-auto">
              <h3 class="text-xl font-bold text-slate-900 dark:text-white mb-3 flex items-center justify-center gap-2">
                <UIcon name="i-heroicons-envelope" class="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                Stay updated
              </h3>
              <p class="text-slate-600 dark:text-slate-400 mb-6">Subscribe to our newsletter for the latest content</p>
              <form @submit.prevent="subscribeNewsletter" class="flex flex-col sm:flex-row gap-3">
                <input
                    type="email"
                    v-model="email"
                    placeholder="Your email address"
                    class="flex-1 px-4 py-3 rounded-lg bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
                    required
                />
                <button
                    type="submit"
                    :disabled="subscribing"
                    class="px-6 py-3 bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-medium rounded-lg hover:from-indigo-600 hover:to-purple-700 transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-75 disabled:cursor-not-allowed"
                >
                  <span v-if="subscribing" class="flex items-center">
                    <UIcon name="i-heroicons-arrow-path" class="animate-spin -ml-1 mr-2 h-4 w-4" />
                    Subscribing...
                  </span>
                  <span v-else>Subscribe</span>
                </button>
              </form>
              <p v-if="subscribeMessage" class="mt-3 text-sm" :class="subscribeSuccess ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'">
                {{ subscribeMessage }}
              </p>
            </div>
          </div>

          <!-- Enhanced Comments Section -->
          <div class="mt-16 pt-10 border-t border-slate-200 dark:border-slate-800">
            <h3 class="text-xl font-semibold text-slate-900 dark:text-white mb-8 flex items-center justify-center gap-2">
              <UIcon name="i-heroicons-chat-bubble-left-ellipsis" class="w-5 h-5 text-blue-500" />
              Comments ({{ comments.length }})
            </h3>

            <!-- Comment Form -->
            <div class="bg-slate-50 dark:bg-slate-800/50 rounded-xl p-6 mb-8">
              <h4 class="font-medium text-slate-900 dark:text-white mb-4">Leave a comment</h4>
              <form @submit.prevent="submitComment" class="space-y-4">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <input
                      type="text"
                      v-model="commentForm.name"
                      placeholder="Your name"
                      class="px-4 py-3 rounded-lg bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      required
                  />
                  <input
                      type="email"
                      v-model="commentForm.email"
                      placeholder="Your email"
                      class="px-4 py-3 rounded-lg bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      required
                  />
                </div>
                <textarea
                    v-model="commentForm.content"
                    placeholder="Your comment"
                    rows="4"
                    class="w-full px-4 py-3 rounded-lg bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    required
                ></textarea>
                <div class="flex justify-end">
                  <button
                      type="submit"
                      :disabled="submittingComment"
                      class="px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-medium rounded-lg hover:from-blue-600 hover:to-indigo-700 transition-all duration-300 shadow-md hover:shadow-lg disabled:opacity-75 disabled:cursor-not-allowed"
                  >
                    <span v-if="submittingComment" class="flex items-center">
                      <UIcon name="i-heroicons-arrow-path" class="animate-spin -ml-1 mr-2 h-4 w-4" />
                      Posting...
                    </span>
                    <span v-else>Post Comment</span>
                  </button>
                </div>
              </form>
            </div>

            <!-- Comments List -->
            <div class="space-y-6">
              <div v-for="(comment, index) in comments" :key="index"
                   class="bg-white dark:bg-slate-800/50 rounded-xl p-6 shadow-sm border border-slate-200 dark:border-slate-700">
                <div class="flex items-start gap-4">
                  <div class="flex-shrink-0">
                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-blue-100 to-indigo-100 dark:from-blue-900/50 dark:to-indigo-900/50 flex items-center justify-center">
                      <UIcon name="i-heroicons-user" class="w-5 h-5 text-blue-600 dark:text-blue-400" />
                    </div>
                  </div>
                  <div class="flex-1">
                    <div class="flex items-center justify-between">
                      <h5 class="font-semibold text-slate-900 dark:text-white">{{ comment.name }}</h5>
                      <span class="text-xs text-slate-500 dark:text-slate-400">{{ formatDate(comment.createdAt) }}</span>
                    </div>
                    <p class="text-slate-700 dark:text-slate-300 mt-2">{{ comment.content }}</p>
                    <div class="flex items-center gap-4 mt-3">
                      <button class="text-sm text-slate-500 dark:text-slate-400 hover:text-blue-600 dark:hover:text-blue-400 transition-colors flex items-center gap-1">
                        <UIcon name="i-heroicons-thumb-up" class="w-4 h-4" />
                        <span>Like</span>
                      </button>
                      <button class="text-sm text-slate-500 dark:text-slate-400 hover:text-blue-600 dark:hover:text-blue-400 transition-colors flex items-center gap-1">
                        <UIcon name="i-heroicons-chat-bubble-left" class="w-4 h-4" />
                        <span>Reply</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </main>



    <!-- Toast Notification -->
    <div
        v-if="toast.show"
        class="fixed bottom-4 left-1/2 transform -translate-x-1/2 px-6 py-3 rounded-lg shadow-lg text-white z-50 transition-all duration-300"
        :class="toast.type === 'success' ? 'bg-green-500' : 'bg-red-500'"
    >
      <div class="flex items-center gap-2">
        <UIcon :name="toast.type === 'success' ? 'i-heroicons-check-circle' : 'i-heroicons-x-circle'" class="w-5 h-5" />
        <span>{{ toast.message }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { parseMarkdown } from '@nuxtjs/mdc/runtime'
import { useBlogStore } from "~/stores/blog";
import type { BlogModel } from "~/types/models/BlogModel";

const blogStore = useBlogStore();
const route = useRoute();
const router = useRouter();
const slug = route.params.slug as string;

// State for blog post
const post = ref<BlogModel>({
  id: '',
  title: '',
  slug: '',
  excerpt: '',
  content: '',
  coverImage: '',
  published: false,
  isActive: true,
  metaTitle: '',
  metaDescription: '',
  categories: [],
  tags: [],
  viewCount: 0,
  createdAt: new Date(),
  updatedAt: new Date()
});

// Loading state
const loading = ref(true);
const error = ref<string | null>(null);

// Interactive states
const copied = ref(false);
const email = ref('');
const subscribing = ref(false);
const subscribeMessage = ref('');
const subscribeSuccess = ref(false);
const showBackToTop = ref(false);
const submittingComment = ref(false);

// Comment form
const commentForm = ref({
  name: '',
  email: '',
  content: ''
});

// Toast notification
const toast = ref({
  show: false,
  message: '',
  type: 'success' as 'success' | 'error'
});

// Mock related posts data
const relatedPosts = ref([
  {
    id: '1',
    title: 'Getting Started with Vue 3 Composition API',
    excerpt: 'Learn how to use Vue 3 Composition API to build more reusable and maintainable components.',
    slug: 'getting-started-vue3-composition-api',
    createdAt: new Date('2023-06-15')
  },
  {
    id: '2',
    title: 'Building Responsive Layouts with Tailwind CSS',
    excerpt: 'Discover how to create beautiful responsive layouts using Tailwind CSS utility classes.',
    slug: 'responsive-layouts-tailwind-css',
    createdAt: new Date('2023-05-28')
  }
]);

// Mock comments data
const comments = ref([
  {
    id: '1',
    name: 'Alex Johnson',
    content: 'This article was really helpful! I learned a lot from it.',
    createdAt: new Date('2023-07-10')
  },
  {
    id: '2',
    name: 'Sarah Williams',
    content: 'Great explanation of complex topics. Looking forward to more articles like this.',
    createdAt: new Date('2023-07-08')
  }
]);

// Fetch blog post by slug
async function fetchBlogPost() {
  loading.value = true;
  error.value = null;
  try {
    const response = await blogStore.getBlogBySlugPublic(slug);
    if (response && response.code === 200 && response.data) {
      post.value = response.data;
    } else {
      throw new Error(response?.message?.[0] || 'Failed to fetch blog post');
    }
  } catch (err) {
    console.error('Error fetching blog post:', err);
    error.value = err instanceof Error ? err.message : 'Unknown error occurred';
  } finally {
    loading.value = false;
  }
}

// Calculate reading time (rough estimate)
const readingTime = computed(() => {
  if (!post.value.content) return 0;
  const wordsPerMinute = 200;
  const wordCount = post.value.content.split(/\s+/).length;
  return Math.ceil(wordCount / wordsPerMinute);
});

// Check if post has cover image
const hasCover = computed(() => Boolean(post.value.coverImage));

// Format date
function formatDate(dateString?: string | Date | null) {
  if (!dateString) return 'No date';
  const date = typeof dateString === 'string' ? new Date(dateString) : dateString;
  if (isNaN(date.getTime())) return 'Invalid date';
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(date);
}

// Format number with K/M suffix for large numbers
function formatNumber(num: number): string {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
}

// Copy to clipboard function
function copyToClipboard() {
  const url = window.location.href;
  navigator.clipboard.writeText(url).then(() => {
    copied.value = true;
    showToast('Link copied to clipboard!', 'success');
    setTimeout(() => {
      copied.value = false;
    }, 2000);
  }).catch(err => {
    console.error('Failed to copy: ', err);
    showToast('Failed to copy link', 'error');
  });
}

// Share post function
function sharePost() {
  if (navigator.share) {
    navigator.share({
      title: post.value.title,
      text: post.value.excerpt,
      url: window.location.href,
    })
        .then(() => console.log('Successful share'))
        .catch((error) => console.log('Error sharing', error));
  } else {
    copyToClipboard();
  }
}

// Navigate to related post
function navigateToPost(postSlug: string) {
  router.push(`/blog/${postSlug}`);
}

// Subscribe to newsletter
async function subscribeNewsletter() {
  if (!email.value) return;

  subscribing.value = true;

  // Simulate API call
  setTimeout(() => {
    subscribing.value = false;
    subscribeSuccess.value = true;
    subscribeMessage.value = 'Successfully subscribed to newsletter!';
    email.value = '';

    setTimeout(() => {
      subscribeMessage.value = '';
    }, 3000);
  }, 1500);
}

// Submit comment
async function submitComment() {
  if (!commentForm.value.name || !commentForm.value.email || !commentForm.value.content) return;

  submittingComment.value = true;

  // Simulate API call
  setTimeout(() => {
    // Add new comment to the list
    comments.value.unshift({
      id: Date.now().toString(),
      name: commentForm.value.name,
      content: commentForm.value.content,
      createdAt: new Date()
    });

    // Reset form
    commentForm.value = {
      name: '',
      email: '',
      content: ''
    };

    submittingComment.value = false;
    showToast('Comment posted successfully!', 'success');
  }, 1500);
}

// Show toast notification
function showToast(message: string, type: 'success' | 'error') {
  toast.value = {
    show: true,
    message,
    type
  };

  setTimeout(() => {
    toast.value.show = false;
  }, 3000);
}



// Handle scroll event for back to top button
onMounted(() => {
  window.addEventListener('scroll', () => {
    showBackToTop.value = window.scrollY > 300;
  });

  fetchBlogPost();
});

// Watch for slug changes (if navigating between posts)
watch(() => route.params.slug, async (newSlug) => {
  if (newSlug && newSlug !== slug) {
    await fetchBlogPost();
  }
});

/** --------- Parser tahan banting --------- */
async function safeParseMDC(src: string) {
  // 1) Coba dengan plugin (jika tersedia)
  try {
    const r = await parseMarkdown(src, {
      highlight: {
        theme: { light: 'github-light', dark: 'github-dark' },
      },
      remark: {
        plugins: {
          // Jika paket tidak terpasang, akan dilempar → tertangkap di catch
          'remark-math': {},
          'rehype-katex': {}
        }
      }
    });
    if (r?.body) return r;
  } catch { /* ignore */ }

  // 2) Coba tanpa plugin
  try {
    const r = await parseMarkdown(src);
    if (r?.body) return r;
  } catch { /* ignore */ }

  // 3) Gagal total → fallback kosong (akan render konten mentah)
  return { body: '', data: {} };
}

// Parse markdown content
const { data: parsed, pending } = await useAsyncData(
    `post-mdc-${slug}`,
    () => post.value.content ? safeParseMDC(post.value.content) : Promise.resolve({ body: '', data: {} }),
    {
      // Hindari status error; selalu return fallback agar UI tetap jalan
      server: true,
      watch: [post]
    }
);

// Gunakan objek aman di template
const parsedSafe = computed(() => parsed?.value ?? { body: '', data: {} });

/** --------- SEO --------- */
useSeoMeta({
  title: computed(() => post.value.metaTitle || post.value.title || 'Blog Post'),
  ogTitle: computed(() => post.value.metaTitle || post.value.title || 'Blog Post'),
  description: computed(() => post.value.metaDescription || post.value.excerpt || ''),
  ogDescription: computed(() => post.value.metaDescription || post.value.excerpt || ''),
  ogImage: computed(() => post.value.coverImage || ''),
  twitterCard: 'summary_large_image'
});
</script>

<style>
/* Custom animations */
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse-slow {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(45deg);
  }
  50% {
    transform: translateY(-10px) rotate(45deg);
  }
}

@keyframes float-reverse {
  0%, 100% {
    transform: translateY(0) rotate(12deg);
  }
  50% {
    transform: translateY(-15px) rotate(12deg);
  }
}

@keyframes float-slow {
  0%, 100% {
    transform: translateY(0) rotate(-12deg);
  }
  50% {
    transform: translateY(-8px) rotate(-12deg);
  }
}

.animate-fade-in-up {
  animation: fade-in-up 0.6s ease-out forwards;
}

.animate-pulse-slow {
  animation: pulse-slow 8s ease-in-out infinite;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.animate-float-reverse {
  animation: float-reverse 7s ease-in-out infinite;
}

.animate-float-slow {
  animation: float-slow 9s ease-in-out infinite;
}

.animation-delay-200 {
  animation-delay: 200ms;
}

.animation-delay-300 {
  animation-delay: 300ms;
}

.animation-delay-400 {
  animation-delay: 400ms;
}

.delay-700 {
  animation-delay: 700ms;
}

.delay-1000 {
  animation-delay: 1000ms;
}

.delay-1500 {
  animation-delay: 1500ms;
}

/* Enhanced prose styling for dark mode */
.prose-dark {
  color: #e2e8f0;
}

.prose-dark h1,
.prose-dark h2,
.prose-dark h3,
.prose-dark h4,
.prose-dark h5,
.prose-dark h6 {
  color: #f1f5f9;
}

.prose-dark a {
  color: #93c5fd;
}

.prose-dark code {
  color: #e2e8f0;
  background-color: #1e293b;
}

.prose-dark pre {
  background-color: #1e293b;
  color: #e2e8f0;
}

.prose-dark blockquote {
  color: #cbd5e1;
  border-left-color: #3b82f6;
}

/* Smooth transitions for all interactive elements */
a, button, .hover-scale {
  transition: all 0.3s ease;
}

/* Enhanced image styling */
.prose img {
  border-radius: 0.75rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.prose img:hover {
  transform: scale(1.02);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* Enhanced code block styling */
.prose pre {
  border-radius: 0.75rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  overflow-x: auto;
}

.dark .prose pre {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.3), 0 2px 4px -1px rgba(0, 0, 0, 0.2);
}

/* Enhanced blockquote styling */
.prose blockquote {
  border-left-width: 0.25rem;
  padding-left: 1rem;
  font-style: italic;
  position: relative;
}

.prose blockquote::before {
  content: "\201C"; /* Fixed escaped quote */
  position: absolute;
  top: -0.5rem;
  left: -0.5rem;
  font-size: 3rem;
  color: #3b82f6;
  opacity: 0.3;
  font-family: Georgia, serif;
}

/* Enhanced link styling */
.prose a {
  text-decoration: none;
  font-weight: 500;
  position: relative;
}

.prose a::after {
  content: "";
  position: absolute;
  width: 0;
  height: 2px;
  bottom: -2px;
  left: 0;
  background-color: #3b82f6;
  transition: width 0.3s ease;
}

.prose a:hover::after {
  width: 100%;
}

.dark .prose a::after {
  background-color: #93c5fd;
}

/* Line clamp utility */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>