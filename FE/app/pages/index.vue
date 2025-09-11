<template>
  <div class="min-h-screen flex flex-col transition-colors duration-300 dark:bg-black bg-gray-100">
    <!-- Mode Toggle Button -->

    <!-- Digital Rain Background -->
    <canvas ref="digitalRainCanvas" class="absolute inset-0 w-full h-full pointer-events-none z-0"></canvas>

    <!-- Full Terminal Display -->
    <div class="absolute top-0 left-0 right-0 bottom-0 z-20 flex flex-col">
      <div class="flex-1 p-4">
        <div
            class="h-full max-w-6xl mx-auto backdrop-blur-sm rounded-lg overflow-hidden shadow-2xl flex flex-col transition-colors duration-300 dark:bg-black/90 dark:border-green-500/30 bg-white/90 border-gray-300"
        >
          <!-- Terminal Header -->
          <div
              class="flex items-center justify-between px-4 py-2 transition-colors duration-300 dark:bg-gray-900 border-b dark:border-green-500/30 bg-gray-100 border-b border-gray-300"
          >
            <div class="flex items-center space-x-2">
              <div class="w-3 h-3 bg-red-500 rounded-full"></div>
              <div class="w-3 h-3 bg-yellow-500 rounded-full"></div>
              <div class="w-3 h-3 bg-green-500 rounded-full"></div>
            </div>
            <div
                class="font-mono text-sm transition-colors duration-300 dark:text-green-400 text-gray-800"
            >
              CYBER DEFENSE TERMINAL v2.0
            </div>
            <div
                class="font-mono text-sm transition-colors duration-300 dark:text-green-400 text-gray-800"
            >
              {{ new Date().toLocaleTimeString() }}
            </div>
          </div>

          <!-- Terminal Content -->
          <div class="flex-1 flex">
            <!-- Left Panel - System Status -->
            <div
                class="w-1/4 p-4 transition-colors duration-300 dark:border-r dark:border-green-500/30 border-r border-gray-300"
            >
              <div
                  class="font-mono text-sm mb-4 transition-colors duration-300 dark:text-green-400 text-gray-800"
              >
                SYSTEM STATUS
              </div>

              <!-- Firewall Status -->
              <div class="mb-4">
                <div class="flex justify-between text-xs mb-1 transition-colors duration-300 dark:text-green-400 text-gray-700">
                  <span>FIREWALL</span>
                  <span>{{ firewallStatus }}%</span>
                </div>
                <div class="w-full rounded-full h-2 transition-colors duration-300 dark:bg-gray-800 bg-gray-200" >
                  <div
                      class="h-2 rounded-full transition-all duration-500 dark:bg-green-500 bg-green-600"
                      :style="{ width: firewallStatus + '%' }"
                  ></div>
                </div>
              </div>

              <!-- Shield Status -->
              <div class="mb-4">
                <div class="flex justify-between text-xs mb-1 transition-colors duration-300 dark:text-green-400 text-gray-700">
                  <span>SHIELD</span>
                  <span>{{ shieldStatus }}%</span>
                </div>
                <div class="w-full rounded-full h-2 transition-colors duration-300 dark:bg-gray-800 bg-gray-200">
                  <div
                      class="h-2 rounded-full transition-all duration-500 dark:bg-blue-500 bg-blue-600"
                      :style="{ width: shieldStatus + '%' }"
                  ></div>
                </div>
              </div>

              <!-- Threat Level -->
              <div class="mb-4">
                <div class="flex justify-between text-xs mb-1 transition-colors duration-300 dark:text-green-400 text-gray-700">
                  <span>THREAT LEVEL</span>
                  <span>{{ threatLevel }}%</span>
                </div>
                <div class="w-full rounded-full h-2 transition-colors duration-300 dark:bg-gray-800 bg-gray-200">
                  <div
                      class="h-2 rounded-full transition-all duration-500 dark:bg-red-500 bg-red-600"
                      :style="{ width: threatLevel + '%' }"
                  ></div>
                </div>
              </div>

              <!-- Active Connections -->
              <div class="mt-6">
                <div
                    class="font-mono text-sm mb-2 transition-colors duration-300 dark:text-green-400 text-gray-800"
                >
                  ACTIVE CONNECTIONS
                </div>
                <div class="space-y-1">
                  <div
                      class="text-xs transition-colors duration-300 dark:text-green-300 text-gray-700"
                  >
                    192.168.1.1:8080
                  </div>
                  <div
                      class="text-xs transition-colors duration-300 dark:text-green-300 text-gray-700"
                  >
                    10.0.0.5:443
                  </div>
                  <div
                      class="text-xs transition-colors duration-300 dark:text-green-300 text-gray-700"
                  >
                    172.16.0.8:22
                  </div>
                </div>
              </div>
            </div>

            <!-- Center Panel - Security Visualization -->
            <div class="flex-1 relative">
              <canvas ref="securityCanvas" class="absolute inset-0 w-full h-full"></canvas>
            </div>

            <!-- Right Panel - Security Log -->
            <div
                class="w-1/4 p-4 transition-colors duration-300 dark:border-l dark:border-green-500/30 border-l border-gray-300"
            >
              <div
                  class="font-mono text-sm mb-4 transition-colors duration-300 dark:text-green-400 text-gray-800"
              >
                SECURITY LOG
              </div>
              <div class="space-y-2 text-xs font-mono max-h-150 overflow-y-auto">
                <div
                    v-for="(log, index) in securityLogs"
                    :key="index"
                    class="p-2 rounded transition-colors duration-300"
                    :class="log.type === 'threat' ? (isDarkMode ? 'bg-red-900/30 text-red-400' : 'bg-red-100 text-red-700') :
                          log.type === 'defense' ? (isDarkMode ? 'bg-blue-900/30 text-blue-400' : 'bg-blue-100 text-blue-700') :
                          (isDarkMode ? 'bg-green-900/30 text-green-400' : 'bg-green-100 text-green-700')"
                >
                  <div :class="isDarkMode ? 'text-gray-400' : 'text-gray-600'">{{ log.time }}</div>
                  <div>{{ log.message }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Terminal Footer -->
          <div
              class="px-4 py-2 transition-colors duration-300 dark:bg-gray-900 border-t dark:border-green-500/30 bg-gray-100 border-t border-gray-300"
          >
            <div class="flex items-center">
              <span
                  class="font-mono text-sm mr-2 transition-colors duration-300 dark:text-green-400 text-gray-800"
              >
                $
              </span>
              <input
                  type="text"
                  class="flex-1 outline-none font-mono text-sm transition-colors duration-300 dark bg-transparent text-green-400"
                  placeholder="Enter command..."
                  v-model="terminalInput"
                  @keyup.enter="executeCommand"
                  disabled
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <AppHeader />
    <main class="flex-grow flex flex-col justify-center items-center relative z-30">
      <div class="w-full max-w-4xl px-4 sm:px-6 lg:px-8 text-center py-12 md:py-16">
        <!-- Card with blur background -->
        <div
            class="rounded-2xl shadow-2xl p-8 md:p-12 transition-colors duration-300 dark:bg-gray-900/70 dark:backdrop-blur-none dark:border dark:border-green-500/20 bg-white/90 backdrop-blur-sm border border-gray-200"
        >
          <div
              class="mb-6 inline-block px-4 py-2 rounded-full backdrop-blur-sm transition-colors duration-300 dark:bg-green-500/10 dark:border dark:border-green-500/30 bg-green-100 border border-green-200"
          >
            <span
                class="text-sm font-medium transition-colors duration-300 dark:text-green-400 text-green-700"
            >
              Developer Portfolio
            </span>
          </div>

          <h1 class="text-4xl md:text-5xl lg:text-6xl font-extrabold mb-6 leading-tight">
            <span class="bg-clip-text text-transparent bg-gradient-to-r from-green-400 to-blue-500">Welcome</span>
            <br />
            <span
                class="transition-colors duration-300 dark:text-gray-300 text-gray-800"
            >
              ringga.dev@gmail.com
            </span>
          </h1>

          <p
              class="text-lg sm:text-xl mb-12 max-w-2xl mx-auto transition-colors duration-300"
              :class="isDarkMode ? 'text-gray-400' : 'text-gray-600'"
          >
            Our AI-powered search is protected by advanced security systems. Watch as our defense system neutralizes threats in real-time.
          </p>

          <div class="flex flex-wrap justify-center gap-4 mb-12">
            <div
                class="px-4 py-3 rounded-xl shadow-sm hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border dark:border-green-500/30 dark:hover:bg-gray-800/70 bg-gray-100 backdrop-blur-sm border border-gray-200 hover:bg-gray-200"
            >
              <div class="flex items-center gap-2">
                <UIcon name="i-heroicons-shield-check" class="w-5 h-5 dark:text-green-400 text-green-600"  />
                <span
                    class="font-medium transition-colors duration-300 dark:text-gray-300 text-gray-800"
                >
                  Android
                </span>
              </div>
            </div>

            <div
                class="px-4 py-3 rounded-xl shadow-sm hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border dark:border-green-500/30 dark:hover:bg-gray-800/70 bg-gray-100 backdrop-blur-sm border border-gray-200 hover:bg-gray-200"
            >
              <div class="flex items-center gap-2">
                <UIcon name="i-heroicons-lock-closed" class="w-5 h-5 dark:text-green-400 text-green-600"  />
                <span
                    class="font-medium transition-colors duration-300 dark:text-gray-300 text-gray-800"
                >
                  Ios
                </span>
              </div>
            </div>

            <div
                class="px-4 py-3 rounded-xl shadow-sm hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border dark:border-green-500/30 dark:hover:bg-gray-800/70 bg-gray-100 backdrop-blur-sm border border-gray-200 hover:bg-gray-200"
            >
              <div class="flex items-center gap-2">
                <UIcon name="i-heroicons-bolt" class="w-5 h-5 dark:text-green-400 text-green-600"  />
                <span
                    class="font-medium transition-colors duration-300 dark:text-gray-300 text-gray-800"
                >
                  Vue JS
                </span>
              </div>
            </div>

            <div
                class="px-4 py-3 rounded-xl shadow-sm hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border dark:border-green-500/30 dark:hover:bg-gray-800/70 bg-gray-100 backdrop-blur-sm border border-gray-200 hover:bg-gray-200"
            >
              <div class="flex items-center gap-2">
                <UIcon name="i-heroicons-server" class="w-5 h-5 dark:text-green-400 text-green-600"  />
                <span
                    class="font-medium transition-colors duration-300 dark:text-gray-300 text-gray-800"

                >
                  Spring Boot
                </span>
              </div>
            </div>
          </div>

          <div class="mt-8">
            <UButton
                size="lg"
                color="primary"
                variant="solid"
                class="px-8 py-3 text-lg font-medium rounded-full shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 dart:bg-green-600 dart:hover:bg-green-500 bg-green-500 hover:bg-green-400"
                icon="i-heroicons-shield-check"
            >
              View Projects
            </UButton>
          </div>
        </div>
      </div>
    </main>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// Mode state dengan colorMode dari Nuxt
const colorMode = useColorMode();
const isDarkMode = computed({
  get() {
    return colorMode.value === "dark";
  },
  set() {
    colorMode.preference = colorMode.value === "dark" ? "light" : "dark";
  },
});

// Watch untuk color mode changes
watch(isDarkMode, () => {
  // Reinitialize animations saat mode berubah
  if (digitalRainCanvas.value && securityCanvas.value) {
    initDigitalRain();
    initSecurityAnimation();
  }
});

const digitalRainCanvas = ref<HTMLCanvasElement | null>(null);
const securityCanvas = ref<HTMLCanvasElement | null>(null);
const terminalInput = ref('');

// System status
const firewallStatus = ref(100);
const shieldStatus = ref(100);
const threatLevel = ref(0);

// Security logs
const securityLogs = ref<{ time: string; message: string; type: 'info' | 'threat' | 'defense' }[]>([]);

// Add security log
const addSecurityLog = (message: string, type: 'info' | 'threat' | 'defense' = 'info') => {
  const now = new Date();
  const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`;
  securityLogs.value.unshift({ time, message, type });
  // Keep only last 20 logs
  if (securityLogs.value.length > 20) {
    securityLogs.value.pop();
  }
};

// Execute terminal command
const executeCommand = () => {
  const command = terminalInput.value.trim().toLowerCase();
  if (command === 'help') {
    addSecurityLog('Available commands: help, status, scan, defend, reset', 'info');
  } else if (command === 'status') {
    addSecurityLog(`Firewall: ${firewallStatus.value}% | Shield: ${shieldStatus.value}% | Threat: ${threatLevel.value}%`, 'info');
  } else if (command === 'scan') {
    addSecurityLog('Scanning for threats...', 'info');
    setTimeout(() => {
      addSecurityLog('Threat detected! Initiating defense protocols.', 'threat');
    }, 1000);
  } else if (command === 'defend') {
    addSecurityLog('Defense protocols activated. Shield strength increased.', 'defense');
    shieldStatus.value = Math.min(100, shieldStatus.value + 20);
  } else if (command === 'reset') {
    addSecurityLog('System reset initiated.', 'info');
    firewallStatus.value = 100;
    shieldStatus.value = 100;
    threatLevel.value = 0;
  } else if (command) {
    addSecurityLog(`Command not found: ${command}. Type 'help' for available commands.`, 'info');
  }
  terminalInput.value = '';
};

// Digital Rain Animation
const initDigitalRain = () => {
  const canvas = digitalRainCanvas.value;
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // Set canvas size
  const resizeCanvas = () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  };
  resizeCanvas();
  window.addEventListener('resize', resizeCanvas);

  // Characters for the rain
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%^&*()_+-=[]{}|;:,.<>?';
  const fontSize = 14;
  const columns = canvas.width / fontSize;

  // Drops array
  const drops: number[] = [];
  for (let i = 0; i < columns; i++) {
    drops[i] = Math.random() * canvas.height;
  }

  // Animation loop
  const animate = () => {
    // Semi-transparent black for trail effect
    ctx.fillStyle = isDarkMode.value ? 'rgba(0, 0, 0, 0.05)' : 'rgba(255, 255, 255, 0.05)';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Set font
    ctx.font = `${fontSize}px monospace`;

    // Draw characters
    for (let i = 0; i < drops.length; i++) {
      // Random character
      const text = characters.charAt(Math.floor(Math.random() * characters.length));

      // Random color based on mode
      if (isDarkMode.value) {
        // Random green shade for dark mode
        const greenShade = Math.floor(Math.random() * 155) + 100;
        ctx.fillStyle = `rgb(0, ${greenShade}, 0)`;
      } else {
        // Random blue shade for light mode
        const blueShade = Math.floor(Math.random() * 155) + 100;
        ctx.fillStyle = `rgb(0, 0, ${blueShade})`;
      }

      // Draw character
      ctx.fillText(text, i * fontSize, drops[i] * fontSize);

      // Reset drop if it reaches bottom or randomly
      if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
        drops[i] = 0;
      }

      // Move drop down
      drops[i]++;
    }

    requestAnimationFrame(animate);
  };

  animate();

  // Clean up
  onUnmounted(() => {
    window.removeEventListener('resize', resizeCanvas);
  });
};

// Security System Visualization
class SecurityNode {
  x: number;
  y: number;
  radius: number;
  color: string;
  pulsePhase: number;
  connections: SecurityNode[];
  type: 'server' | 'firewall' | 'shield' | 'data';

  constructor(x: number, y: number, radius: number, color: string, type: 'server' | 'firewall' | 'shield' | 'data') {
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.color = color;
    this.pulsePhase = Math.random() * Math.PI * 2;
    this.connections = [];
    this.type = type;
  }

  update() {
    this.pulsePhase += 0.02;
  }

  draw(ctx: CanvasRenderingContext2D) {
    // Draw connections
    this.connections.forEach(node => {
      ctx.save();
      ctx.globalAlpha = 0.3;
      ctx.strokeStyle = this.color;
      ctx.lineWidth = 1;
      ctx.beginPath();
      ctx.moveTo(this.x, this.y);
      ctx.lineTo(node.x, node.y);
      ctx.stroke();
      ctx.restore();
    });

    // Draw node with pulse effect
    const pulseSize = this.radius + Math.sin(this.pulsePhase) * 3;

    // Glow effect
    ctx.save();
    ctx.globalAlpha = 0.3;
    const glowGradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, pulseSize * 3);
    glowGradient.addColorStop(0, this.color);
    glowGradient.addColorStop(1, 'transparent');
    ctx.fillStyle = glowGradient;
    ctx.beginPath();
    ctx.arc(this.x, this.y, pulseSize * 3, 0, Math.PI * 2);
    ctx.fill();
    ctx.restore();

    // Node circle
    ctx.fillStyle = this.color;
    ctx.beginPath();
    ctx.arc(this.x, this.y, pulseSize, 0, Math.PI * 2);
    ctx.fill();

    // Inner core
    ctx.fillStyle = '#ffffff';
    ctx.beginPath();
    ctx.arc(this.x, this.y, pulseSize * 0.5, 0, Math.PI * 2);
    ctx.fill();

    // Draw icon based on type
    ctx.fillStyle = '#000000';
    ctx.font = 'bold 12px monospace';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    switch (this.type) {
      case 'server':
        ctx.fillText('SVR', this.x, this.y);
        break;
      case 'firewall':
        ctx.fillText('FW', this.x, this.y);
        break;
      case 'shield':
        ctx.fillText('SH', this.x, this.y);
        break;
      case 'data':
        ctx.fillText('DT', this.x, this.y);
        break;
    }
  }
}

// Threat class
class Threat {
  x: number;
  y: number;
  targetX: number;
  targetY: number;
  size: number;
  color: string;
  speed: number;
  type: 'malware' | 'phishing' | 'ddos' | 'exploit' | 'virus';
  trail: { x: number; y: number }[];

  constructor(x: number, y: number, targetX: number, targetY: number, type: 'malware' | 'phishing' | 'ddos' | 'exploit' | 'virus') {
    this.x = x;
    this.y = y;
    this.targetX = targetX;
    this.targetY = targetY;
    this.type = type;
    this.trail = [];

    // Set properties based on threat type
    switch (type) {
      case 'malware':
        this.size = 8;
        this.color = '#ef4444';
        this.speed = 3;
        break;
      case 'phishing':
        this.size = 6;
        this.color = '#f59e0b';
        this.speed = 4;
        break;
      case 'ddos':
        this.size = 12;
        this.color = '#8b5cf6';
        this.speed = 2;
        break;
      case 'exploit':
        this.size = 7;
        this.color = '#ec4899';
        this.speed = 5;
        break;
      case 'virus':
        this.size = 9;
        this.color = '#10b981';
        this.speed = 3.5;
        break;
    }
  }

  update() {
    // Add current position to trail
    this.trail.push({ x: this.x, y: this.y });

    // Keep trail length limited
    if (this.trail.length > 10) {
      this.trail.shift();
    }

    const dx = this.targetX - this.x;
    const dy = this.targetY - this.y;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < this.speed) {
      return false; // Reached target
    }

    this.x += (dx / distance) * this.speed;
    this.y += (dy / distance) * this.speed;

    return true;
  }

  draw(ctx: CanvasRenderingContext2D) {
    // Draw trail
    if (this.trail.length > 1) {
      const gradient = ctx.createLinearGradient(
          this.trail[0].x, this.trail[0].y,
          this.x, this.y
      );
      gradient.addColorStop(0, this.color.replace('rgb', 'rgba').replace(')', ', 0)'));
      gradient.addColorStop(1, this.color.replace('rgb', 'rgba').replace(')', ', 0.8)'));

      ctx.strokeStyle = gradient;
      ctx.lineWidth = this.size * 0.6;
      ctx.lineCap = 'round';
      ctx.beginPath();

      this.trail.forEach((point, index) => {
        if (index === 0) {
          ctx.moveTo(point.x, point.y);
        } else {
          ctx.lineTo(point.x, point.y);
        }
      });

      ctx.stroke();
    }

    // Draw threat with glow effect
    ctx.save();

    // Outer glow
    const glowGradient = ctx.createRadialGradient(this.x, this.y, 0, this.x, this.y, this.size * 2);
    glowGradient.addColorStop(0, this.color.replace('rgb', 'rgba').replace(')', ', 0.8)'));
    glowGradient.addColorStop(1, this.color.replace('rgb', 'rgba').replace(')', ', 0)'));

    ctx.fillStyle = glowGradient;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.size * 2, 0, Math.PI * 2);
    ctx.fill();

    // Core
    ctx.fillStyle = this.color;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
    ctx.fill();

    // Inner core
    ctx.fillStyle = '#ffffff';
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.size * 0.3, 0, Math.PI * 2);
    ctx.fill();

    // Draw threat symbol based on type
    ctx.fillStyle = '#000000';
    ctx.font = 'bold 10px monospace';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    switch (this.type) {
      case 'malware':
        ctx.fillText('M', this.x, this.y);
        break;
      case 'phishing':
        ctx.fillText('P', this.x, this.y);
        break;
      case 'ddos':
        ctx.fillText('D', this.x, this.y);
        break;
      case 'exploit':
        ctx.fillText('E', this.x, this.y);
        break;
      case 'virus':
        ctx.fillText('V', this.x, this.y);
        break;
    }

    ctx.restore();
  }
}

// Defense Field class
class DefenseField {
  x: number;
  y: number;
  radius: number;
  strength: number;
  maxStrength: number;
  rechargeRate: number;
  particles: { angle: number; distance: number; size: number; speed: number }[];

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
    this.radius = 150;
    this.strength = 100;
    this.maxStrength = 100;
    this.rechargeRate = 0.3;
    this.particles = [];

    // Initialize shield particles
    for (let i = 0; i < 40; i++) {
      this.particles.push({
        angle: (i / 40) * Math.PI * 2,
        distance: this.radius * 0.8 + Math.random() * this.radius * 0.2,
        size: 2 + Math.random() * 4,
        speed: 0.01 + Math.random() * 0.02
      });
    }
  }

  update() {
    // Recharge shield
    if (this.strength < this.maxStrength) {
      this.strength = Math.min(this.maxStrength, this.strength + this.rechargeRate);
    }

    // Update particles
    this.particles.forEach(particle => {
      particle.angle += particle.speed;
      particle.distance = this.radius * 0.8 + Math.sin(particle.angle * 3) * this.radius * 0.2;
    });
  }

  canBlock() {
    return this.strength > 20;
  }

  blockThreat(threat: Threat) {
    if (!this.canBlock()) return false;

    const dx = threat.x - this.x;
    const dy = threat.y - this.y;
    const distance = Math.sqrt(dx * dx + dy * dy);

    if (distance < this.radius) {
      this.strength = Math.max(0, this.strength - 15);
      return true;
    }

    return false;
  }

  draw(ctx: CanvasRenderingContext2D) {
    ctx.save();

    // Shield strength indicator
    const strengthPercent = this.strength / this.maxStrength;

    // Outer shield with gradient
    const shieldGradient = ctx.createRadialGradient(this.x, this.y, this.radius * 0.6, this.x, this.y, this.radius);
    shieldGradient.addColorStop(0, `rgba(59, 130, 246, ${0.1 * strengthPercent})`);
    shieldGradient.addColorStop(1, `rgba(59, 130, 246, ${0.3 * strengthPercent})`);

    ctx.fillStyle = shieldGradient;
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
    ctx.fill();

    // Inner shield rings
    for (let i = 1; i <= 3; i++) {
      ctx.strokeStyle = `rgba(59, 130, 246, ${0.6 * strengthPercent / i})`;
      ctx.lineWidth = 2;
      ctx.beginPath();
      ctx.arc(this.x, this.y, this.radius * (0.8 - i * 0.1), 0, Math.PI * 2);
      ctx.stroke();
    }

    // Shield particles
    if (this.canBlock()) {
      this.particles.forEach(particle => {
        const x = this.x + Math.cos(particle.angle) * particle.distance;
        const y = this.y + Math.sin(particle.angle) * particle.distance;

        // Particle glow
        const particleGradient = ctx.createRadialGradient(x, y, 0, x, y, particle.size * 2);
        particleGradient.addColorStop(0, 'rgba(59, 130, 246, 0.8)');
        particleGradient.addColorStop(1, 'rgba(59, 130, 246, 0)');

        ctx.fillStyle = particleGradient;
        ctx.beginPath();
        ctx.arc(x, y, particle.size * 2, 0, Math.PI * 2);
        ctx.fill();

        // Particle core
        ctx.fillStyle = 'rgba(255, 255, 255, 0.9)';
        ctx.beginPath();
        ctx.arc(x, y, particle.size, 0, Math.PI * 2);
        ctx.fill();
      });
    }

    ctx.restore();
  }
}

// Particle class for effects
class Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  size: number;
  color: string;
  life: number;
  maxLife: number;
  gravity: number;

  constructor(x: number, y: number, vx: number, vy: number, size: number, color: string, maxLife: number, gravity = 0) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.size = size;
    this.color = color;
    this.life = 0;
    this.maxLife = maxLife;
    this.gravity = gravity;
  }

  update() {
    this.x += this.vx;
    this.y += this.vy;
    this.vy += this.gravity;
    this.life++;
    return this.life < this.maxLife;
  }

  draw(ctx: CanvasRenderingContext2D) {
    const alpha = 1 - (this.life / this.maxLife);
    const size = this.size * (1 - (this.life / this.maxLife) * 0.5);

    ctx.fillStyle = this.color.replace('rgb', 'rgba').replace(')', `, ${alpha})`);
    ctx.beginPath();
    ctx.arc(this.x, this.y, size, 0, Math.PI * 2);
    ctx.fill();
  }
}

// Security Animation
const initSecurityAnimation = () => {
  const canvas = securityCanvas.value;
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // Set canvas size
  const resizeCanvas = () => {
    canvas.width = canvas.parentElement?.clientWidth || window.innerWidth;
    canvas.height = canvas.parentElement?.clientHeight || window.innerHeight;
  };
  resizeCanvas();
  window.addEventListener('resize', resizeCanvas);

  // Create security nodes
  const securityNodes: SecurityNode[] = [];
  const nodeTypes: ('server' | 'firewall' | 'shield' | 'data')[] = ['server', 'firewall', 'shield', 'data'];
  const nodeColors = ['#3b82f6', '#ef4444', '#10b981', '#8b5cf6'];

  // Create nodes in a network pattern
  const centerX = canvas.width / 2;
  const centerY = canvas.height / 2;
  const networkRadius = Math.min(canvas.width, canvas.height) * 0.3;

  // Central shield node
  const centralShield = new SecurityNode(centerX, centerY, 20, '#10b981', 'shield');
  securityNodes.push(centralShield);

  // Surrounding nodes
  for (let i = 0; i < 8; i++) {
    const angle = (i / 8) * Math.PI * 2;
    const x = centerX + Math.cos(angle) * networkRadius;
    const y = centerY + Math.sin(angle) * networkRadius;
    const typeIndex = i % nodeTypes.length;
    const node = new SecurityNode(x, y, 15, nodeColors[typeIndex], nodeTypes[typeIndex]);

    // Connect to central shield
    node.connections.push(centralShield);
    centralShield.connections.push(node);

    securityNodes.push(node);
  }

  // Create defense field
  const defenseField = new DefenseField(centerX, centerY);

  // Arrays for game objects
  const threats: Threat[] = [];
  const particles: Particle[] = [];

  // Threat types
  const threatTypes: ('malware' | 'phishing' | 'ddos' | 'exploit' | 'virus')[] = ['malware', 'phishing', 'ddos', 'exploit', 'virus'];

  // Animation variables
  let lastThreatTime = 0;
  const threatInterval = 2000; // ms between threats

  // Initial logs
  addSecurityLog('Security system initialized', 'defense');
  addSecurityLog('Defense field activated', 'defense');
  addSecurityLog('Monitoring for threats...', 'info');

  // Animation loop
  const animate = (timestamp: number) => {
    // Clear canvas with slight transparency for trail effect
    ctx.fillStyle = isDarkMode.value ? 'rgba(0, 0, 0, 0.1)' : 'rgba(255, 255, 255, 0.1)';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Update security nodes
    securityNodes.forEach(node => {
      node.update();
    });

    // Update defense field
    defenseField.update();

    // Update system status based on defense
    shieldStatus.value = Math.round(defenseField.strength);
    firewallStatus.value = Math.max(0, Math.min(100, firewallStatus.value - 0.01));
    threatLevel.value = Math.min(100, threatLevel.value + 0.01);

    // Create new threats
    if (timestamp - lastThreatTime > threatInterval) {
      const threatType = threatTypes[Math.floor(Math.random() * threatTypes.length)];

      // Random spawn point from edge
      const side = Math.floor(Math.random() * 4);
      let x, y;

      switch (side) {
        case 0: // Top
          x = Math.random() * canvas.width;
          y = 0;
          break;
        case 1: // Right
          x = canvas.width;
          y = Math.random() * canvas.height;
          break;
        case 2: // Bottom
          x = Math.random() * canvas.width;
          y = canvas.height;
          break;
        case 3: // Left
          x = 0;
          y = Math.random() * canvas.height;
          break;
      }

      threats.push(new Threat(
          x,
          y,
          centerX,
          centerY,
          threatType
      ));

      // Log threat
      addSecurityLog(`${threatType.toUpperCase()} threat detected!`, 'threat');

      lastThreatTime = timestamp;
    }

    // Update and draw threats
    for (let i = threats.length - 1; i >= 0; i--) {
      const threat = threats[i];

      if (!threat.update()) {
        // Threat reached target
        if (defenseField.blockThreat(threat)) {
          // Blocked
          addSecurityLog(`${threat.type.toUpperCase()} threat neutralized by defense field`, 'defense');

          // Create block effect
          for (let j = 0; j < 20; j++) {
            const angle = Math.random() * Math.PI * 2;
            const speed = 2 + Math.random() * 4;

            particles.push(new Particle(
                threat.x,
                threat.y,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed,
                2 + Math.random() * 4,
                'rgb(59, 130, 246)',
                40
            ));
          }
        } else {
          // Penetrated
          addSecurityLog(`${threat.type.toUpperCase()} threat penetrated defenses!`, 'threat');

          // Create penetration effect
          for (let j = 0; j < 30; j++) {
            const angle = Math.random() * Math.PI * 2;
            const speed = 1 + Math.random() * 5;

            particles.push(new Particle(
                centerX,
                centerY,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed - 2,
                2 + Math.random() * 5,
                'rgb(239, 68, 68)',
                50,
                0.1
            ));
          }

          // Damage system
          shieldStatus.value = Math.max(0, shieldStatus.value - 10);
          firewallStatus.value = Math.max(0, firewallStatus.value - 5);
          threatLevel.value = Math.min(100, threatLevel.value + 10);
        }

        threats.splice(i, 1);
        continue;
      }

      threat.draw(ctx);
    }

    // Update and draw particles
    for (let i = particles.length - 1; i >= 0; i--) {
      const particle = particles[i];

      if (!particle.update()) {
        particles.splice(i, 1);
        continue;
      }

      particle.draw(ctx);
    }

    // Draw defense field
    defenseField.draw(ctx);

    // Draw security nodes
    securityNodes.forEach(node => {
      node.draw(ctx);
    });

    // Add ambient particles
    if (Math.random() < 0.05) {
      particles.push(new Particle(
          Math.random() * canvas.width,
          Math.random() * canvas.height,
          (Math.random() - 0.5) * 0.5,
          (Math.random() - 0.5) * 0.5,
          1,
          'rgba(59, 130, 246, 0.5)',
          100
      ));
    }

    requestAnimationFrame(animate);
  };

  // Start animation
  requestAnimationFrame(animate);

  // Clean up
  onUnmounted(() => {
    window.removeEventListener('resize', resizeCanvas);
  });
};

// Initialize animations
onMounted(() => {
  initDigitalRain();
  initSecurityAnimation();
});

definePageMeta({
  middleware: 'guest'
});
</script>

<style scoped>
/* Terminal styling */
.terminal-output {
  font-family: 'Courier New', monospace;
  padding: 10px;
  border-radius: 5px;
  height: 150px;
  overflow-y: auto;
}

.terminal-line {
  margin-bottom: 5px;
}

.terminal-line.threat {
  color: #f44;
}

.terminal-line.defense {
  color: #44f;
}

/* Custom scrollbar for terminal */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

::-webkit-scrollbar-thumb {
  background: rgba(16, 185, 129, 0.5);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(16, 185, 129, 0.7);
}

/* Gradient text enhancement */
.bg-gradient-text {
  background: linear-gradient(120deg, #10b981, #3b82f6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
</style>