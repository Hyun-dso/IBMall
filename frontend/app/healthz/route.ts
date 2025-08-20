// app/healthz/route.ts
export async function GET() {
  return new Response("OK", { status: 200 });
}
