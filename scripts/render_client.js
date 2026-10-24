/**
 * FittShop - Render API & Deploy Hook Integration Client
 * Permite que agentes e pipelines consultem status, logs e acionem deploys no Render.
 */

const https = require('https');
const fs = require('fs');
const path = require('path');

// Carrega variáveis do arquivo .env local se existirem
function loadEnv() {
  const envPath = path.resolve(__dirname, '../.env');
  if (fs.existsSync(envPath)) {
    const lines = fs.readFileSync(envPath, 'utf-8').split('\n');
    for (const line of lines) {
      const trimmed = line.trim();
      if (trimmed && !trimmed.startsWith('#') && trimmed.includes('=')) {
        const [k, ...v] = trimmed.split('=');
        if (!process.env[k.trim()]) {
          process.env[k.trim()] = v.join('=').trim();
        }
      }
    }
  }
}

loadEnv();

const RENDER_API_KEY = process.env.RENDER_API_KEY;
const RENDER_SERVICE_ID = process.env.RENDER_SERVICE_ID;
const RENDER_DEPLOY_HOOK_URL = process.env.RENDER_DEPLOY_HOOK_URL;

function renderRequest(method, endpoint, body) {
  return new Promise((resolve, reject) => {
    if (!RENDER_API_KEY) {
      return reject(new Error('RENDER_API_KEY não configurada no .env ou nas variáveis de ambiente.'));
    }
    const payload = body ? JSON.stringify(body) : null;
    const options = {
      hostname: 'api.render.com',
      path: `/v1${endpoint}`,
      method: method,
      headers: {
        'Authorization': `Bearer ${RENDER_API_KEY}`,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    };
    if (payload) {
      options.headers['Content-Length'] = Buffer.byteLength(payload);
    }
    const req = https.request(options, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          resolve({ status: res.statusCode, data: JSON.parse(data) });
        } catch {
          resolve({ status: res.statusCode, raw: data });
        }
      });
    });
    req.on('error', reject);
    if (payload) req.write(payload);
    req.end();
  });
}

async function triggerDeployHook(url) {
  return new Promise((resolve, reject) => {
    const targetUrl = new URL(url);
    const req = https.request({
      hostname: targetUrl.hostname,
      path: targetUrl.pathname + targetUrl.search,
      method: 'POST'
    }, (res) => {
      let data = '';
      res.on('data', c => data += c);
      res.on('end', () => resolve({ status: res.statusCode, body: data }));
    });
    req.on('error', reject);
    req.end();
  });
}

async function main() {
  const args = process.argv.slice(2);
  const command = args[0] || 'status';

  console.log('================================================================================');
  console.log('🚀 FITTSHOP - CLIENTE DE INTEGRAÇÃO OFICIAL RENDER API');
  console.log('================================================================================');

  if (command === 'hook') {
    const hookUrl = args[1] || RENDER_DEPLOY_HOOK_URL;
    if (!hookUrl) {
      console.error('❌ Erro: Deploy Hook URL não informado nem configurado em RENDER_DEPLOY_HOOK_URL.');
      process.exit(1);
    }
    console.log('Acionando Deploy via Hook oficial:', hookUrl.substring(0, 35) + '...');
    const res = await triggerDeployHook(hookUrl);
    console.log(`Status HTTP: ${res.status} | Resposta:`, res.body);
    return;
  }

  if (!RENDER_API_KEY) {
    console.log('⚠️ AVISO: RENDER_API_KEY não foi encontrada.');
    console.log('Para conectar diretamente à API do Render:');
    console.log('1. Gere uma API Key em: https://dashboard.render.com/u/settings#api-keys');
    console.log('2. Adicione ao arquivo .env:');
    console.log('   RENDER_API_KEY=rnd_xxxxxxxxxxxxxxxxxxxxxxxx');
    console.log('   RENDER_SERVICE_ID=srv-xxxxxxxxxxxxxxxxxxxx (opcional, pode ser listado automaticamente)');
    console.log('--------------------------------------------------------------------------------');
    console.log('Alternativa rápida: Configure RENDER_DEPLOY_HOOK_URL no .env (copiado de Settings -> Deploy Hook).');
    return;
  }

  try {
    if (command === 'services') {
      console.log('Consultando serviços vinculados na conta do Render...');
      const res = await renderRequest('GET', '/services?limit=20');
      console.log(`Status: ${res.status}`);
      if (Array.isArray(res.data)) {
        res.data.forEach(item => {
          const s = item.service;
          console.log(`- [${s.id}] ${s.name} (${s.type}) | Repo: ${s.repo} | Status: ${s.suspended ? 'SUSPENDED' : 'ACTIVE'}`);
        });
      }
    } else if (command === 'deploys') {
      const srvId = args[1] || RENDER_SERVICE_ID;
      if (!srvId) {
        console.error('❌ Informe o Service ID: node scripts/render_client.js deploys srv-xxxx');
        process.exit(1);
      }
      console.log(`Consultando histórico de deploys do serviço ${srvId}...`);
      const res = await renderRequest('GET', `/services/${srvId}/deploys?limit=5`);
      console.log(`Status: ${res.status}`);
      if (Array.isArray(res.data)) {
        res.data.forEach(d => {
          const dep = d.deploy;
          console.log(`- [${dep.id}] Status: ${dep.status} | Commit: ${dep.commit?.id?.substring(0, 7)} - "${dep.commit?.message}" | Criado em: ${dep.createdAt}`);
        });
      }
    } else if (command === 'deploy') {
      const srvId = args[1] || RENDER_SERVICE_ID;
      if (!srvId) {
        console.error('❌ Informe o Service ID: node scripts/render_client.js deploy srv-xxxx');
        process.exit(1);
      }
      console.log(`Disparando novo deploy para o serviço ${srvId}...`);
      const res = await renderRequest('POST', `/services/${srvId}/deploys`, { clearCache: 'do_not_clear' });
      console.log(`Deploy disparado! Status: ${res.status}`, res.data);
    } else {
      console.log('Comandos disponíveis:');
      console.log('  node scripts/render_client.js services         - Lista todos os serviços no Render');
      console.log('  node scripts/render_client.js deploys <srv-id> - Histórico de deploys de um serviço');
      console.log('  node scripts/render_client.js deploy <srv-id>  - Dispara um novo deploy');
      console.log('  node scripts/render_client.js hook <url>       - Dispara deploy via webhook direto');
    }
  } catch (err) {
    console.error('❌ Erro na integração com a API do Render:', err.message);
  }
}

main();
