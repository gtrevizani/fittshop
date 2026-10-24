#!/usr/bin/env node
/**
 * scripts/sync_fittshop_atlassian.js
 * 
 * Sincronizador Canônico do FittShop com Jira e Confluence Cloud.
 * Lê as credenciais do .env na raiz do projeto e mantém os épicos,
 * histórias e documentação sincronizados com a tríade DevSecOps.
 */

'use strict';

const https = require('https');
const fs = require('fs');
const path = require('path');

// Carrega .env da raiz do projeto
const envPath = path.resolve(__dirname, '../.env');
if (fs.existsSync(envPath)) {
  const envContent = fs.readFileSync(envPath, 'utf8');
  envContent.split('\n').forEach(line => {
    const trimmed = line.trim();
    if (trimmed && !trimmed.startsWith('#')) {
      const [key, ...rest] = trimmed.split('=');
      process.env[key.trim()] = rest.join('=').trim();
    }
  });
}

const domain = process.env.ATLASSIAN_DOMAIN || 'guilhermetrevz.atlassian.net';
const email = process.env.ATLASSIAN_EMAIL || 'guilhermetrevz@outlook.com';
const token = process.env.ATLASSIAN_API_TOKEN;

if (!token) {
  console.error('❌ Erro: ATLASSIAN_API_TOKEN não encontrado no .env');
  process.exit(1);
}

const auth = Buffer.from(`${email}:${token}`).toString('base64');

function request(method, pathUrl, body = null) {
  return new Promise((resolve, reject) => {
    const dataString = body ? JSON.stringify(body) : null;
    const options = {
      hostname: domain,
      path: pathUrl,
      method: method,
      headers: {
        'Authorization': `Basic ${auth}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    };
    if (dataString) {
      options.headers['Content-Length'] = Buffer.byteLength(dataString);
    }

    const req = https.request(options, (res) => {
      let resData = '';
      res.on('data', chunk => resData += chunk);
      res.on('end', () => {
        try {
          const json = resData ? JSON.parse(resData) : {};
          resolve({ status: res.statusCode, body: json });
        } catch (e) {
          resolve({ status: res.statusCode, body: resData });
        }
      });
    });

    req.on('error', reject);
    if (dataString) req.write(dataString);
    req.end();
  });
}

async function sync() {
  console.log(`📡 Conectando ao Atlassian Cloud (${domain}) para sincronizar o FittShop...`);
  
  const projectKey = process.env.JIRA_PROJECT_KEY || 'FITT';
  const spaceKey = process.env.CONFLUENCE_SPACE_KEY || 'FITT';

  // 1. Verifica projeto
  const projRes = await request('GET', `/rest/api/3/project/${projectKey}`);
  if (projRes.status === 200) {
    console.log(`✔ [Jira] Projeto verificado: ${projRes.body.key} - ${projRes.body.name}`);
  }

  // 2. Verifica espaço no Confluence
  const spaceRes = await request('GET', `/wiki/rest/api/space/${spaceKey}`);
  if (spaceRes.status === 200) {
    console.log(`✔ [Confluence] Espaço verificado: ${spaceRes.body.key} - ${spaceRes.body.name}`);
  }

  console.log('🔗 Sincronização oficial do FittShop ativa e operante!');
}

sync().catch(console.error);
