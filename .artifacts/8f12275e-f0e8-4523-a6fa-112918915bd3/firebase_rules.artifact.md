# Configuração de Regras do Firebase

O erro `missing or insufficient permission` ocorre porque as regras de segurança do seu Firebase ainda não permitem que o app grave nas novas coleções de Comunidade (`posts` e `communities`).

Como o projeto armazena os dados em coleções na raiz, você precisa atualizar as regras no **Console do Firebase**.

## 1. Firestore Rules

Vá em **Firebase Console > Firestore Database > Rules** e substitua pelo conteúdo abaixo:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Regras existentes para dados do usuário
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // NOVAS REGRAS PARA COMUNIDADE

    // Coleção de Comunidades
    match /communities/{communityId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && resource.data.creatorId == request.auth.uid;
    }

    // Coleção de Posts
    match /posts/{postId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && resource.data.authorId == request.auth.uid;

      // Subcoleção de Likes
      match /likes/{userId} {
        allow read: if request.auth != null;
        allow write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

## 2. Storage Rules

Vá em **Firebase Console > Storage > Rules** e substitua pelo conteúdo abaixo:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    // Permite upload de imagens de posts e comunidades para usuários logados
    match /posts/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }

    match /communities/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }

    // Regras padrão para outros arquivos
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

> [!IMPORTANT]
> Após colar as regras, clique em **"Publish"** (Publicar) no console do Firebase. Pode levar cerca de 1 a 2 minutos para as novas regras entrarem em vigor.
