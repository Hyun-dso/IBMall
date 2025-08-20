name: Backend Rollback (manual)

on:
  workflow_dispatch:
    inputs:
      image:
        description: "Image to roll back to (e.g., ghcr.io/owner/ibmall-backend:<tag>)"
        required: true
        type: string

jobs:
  rollback:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Rollback via Bastion
        run: |
          echo "${{ secrets.BASTION_KEY }}" > bastion.pem
          chmod 600 bastion.pem
          SSH_BASTION="${{ secrets.BASTION_USER }}@${{ secrets.BASTION_HOST }}"
          APP_USER="${{ secrets.APP_USER }}"
          APP1="${{ secrets.APP1_HOST }}"
          APP2="${{ secrets.APP2_HOST }}"

          # 2-hop 키
          scp -o StrictHostKeyChecking=no -i bastion.pem bastion.pem $SSH_BASTION:/tmp/app.pem
          ssh -o StrictHostKeyChecking=no -i bastion.pem $SSH_BASTION "chmod 600 /tmp/app.pem"

          # 두 서버 순차 롤백
          for HOST in "$APP1" "$APP2"; do
            ssh -o StrictHostKeyChecking=no -i bastion.pem $SSH_BASTION \
              "ssh -o StrictHostKeyChecking=no -o IdentitiesOnly=yes -i /tmp/app.pem ${APP_USER}@${HOST} \
               'sudo /home/ubuntu/deploy/backend/rollback.sh \"${{ inputs.image }}\"'"
          done

          # (선택) 키 삭제
          ssh -o StrictHostKeyChecking=no -i bastion.pem $SSH_BASTION 'shred -u /tmp/app.pem' || true
