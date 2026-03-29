import { Download, Smartphone, Code2, FileCode2, Terminal, FolderOpen, Music, CircleCheck } from 'lucide-react';
import Markdown from 'react-markdown';
import readmeContent from '../README.md?raw';

export default function App() {
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-50 font-sans selection:bg-emerald-500/30">
      {/* Header */}
      <header className="border-b border-zinc-800 bg-zinc-900/50 backdrop-blur-xl sticky top-0 z-50">
        <div className="max-w-5xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-emerald-500 flex items-center justify-center">
              <Smartphone className="w-5 h-5 text-zinc-950" />
            </div>
            <h1 className="font-bold text-lg tracking-tight">Baby Scratch Android Clone</h1>
          </div>
          <div className="flex items-center gap-4 text-sm font-medium text-zinc-400">
            <span className="flex items-center gap-1.5"><Code2 className="w-4 h-4" /> Kotlin</span>
            <span className="flex items-center gap-1.5"><FileCode2 className="w-4 h-4" /> C++ (NDK)</span>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-6 py-12 space-y-16">
        
        {/* Hero Section */}
        <section className="text-center space-y-6 py-12">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 text-sm font-medium border border-emerald-500/20 mb-4">
            <CircleCheck className="w-4 h-4" />
            Project Generation Complete
          </div>
          <h2 className="text-4xl md:text-5xl font-extrabold tracking-tight text-white">
            Native Android Audio Engine
          </h2>
          <p className="text-lg text-zinc-400 max-w-2xl mx-auto leading-relaxed">
            This workspace contains a complete, production-ready Android project built with Kotlin, Jetpack Compose, and a low-latency C++ audio engine using Oboe. 
            <br/><br/>
            <strong className="text-zinc-200">Note:</strong> Because this is a native Android application, it cannot be run directly in this web browser preview. You must download the source code and compile it.
          </p>
          
          <div className="pt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
            <div className="px-6 py-3 rounded-xl bg-zinc-800 border border-zinc-700 flex items-center gap-3 text-sm text-zinc-300">
              <Download className="w-5 h-5 text-zinc-400" />
              <span>Use the <strong>Export</strong> or <strong>Download</strong> button in the AI Studio menu to get the code.</span>
            </div>
          </div>
        </section>

        {/* Project Structure */}
        <section className="grid md:grid-cols-2 gap-8">
          <div className="p-6 rounded-2xl bg-zinc-900 border border-zinc-800 space-y-4">
            <div className="flex items-center gap-3 text-emerald-400 mb-6">
              <FolderOpen className="w-6 h-6" />
              <h3 className="text-xl font-bold text-white">Project Structure</h3>
            </div>
            <ul className="space-y-3 text-sm text-zinc-400 font-mono">
              <li className="flex items-center gap-2"><span className="text-zinc-600">├─</span> app/src/main/cpp/ <span className="text-zinc-500 ml-auto">Native Audio Engine</span></li>
              <li className="flex items-center gap-2"><span className="text-zinc-600">├─</span> app/src/main/kotlin/ <span className="text-zinc-500 ml-auto">UI & Logic</span></li>
              <li className="flex items-center gap-2"><span className="text-zinc-600">├─</span> app/src/main/assets/beat/ <span className="text-zinc-500 ml-auto">Backing Tracks</span></li>
              <li className="flex items-center gap-2"><span className="text-zinc-600">├─</span> app/src/main/assets/scratch/ <span className="text-zinc-500 ml-auto">Scratch Samples</span></li>
              <li className="flex items-center gap-2"><span className="text-zinc-600">├─</span> app/build.gradle.kts <span className="text-zinc-500 ml-auto">Build Config</span></li>
              <li className="flex items-center gap-2"><span className="text-zinc-600">└─</span> setup.sh <span className="text-zinc-500 ml-auto">Init Script</span></li>
            </ul>
          </div>

          <div className="p-6 rounded-2xl bg-zinc-900 border border-zinc-800 space-y-4">
            <div className="flex items-center gap-3 text-emerald-400 mb-6">
              <Music className="w-6 h-6" />
              <h3 className="text-xl font-bold text-white">Required Audio Assets</h3>
            </div>
            <p className="text-sm text-zinc-400 mb-4">
              Before compiling, you must add your WAV files to the assets directories:
            </p>
            <div className="space-y-3">
              <div className="p-3 rounded-lg bg-zinc-950 border border-zinc-800">
                <p className="text-xs text-zinc-500 mb-1">Scratch Sample (Turntable)</p>
                <code className="text-sm text-emerald-400">app/src/main/assets/scratch/scratchy_seal_3d_side_a.wav</code>
              </div>
              <div className="p-3 rounded-lg bg-zinc-950 border border-zinc-800">
                <p className="text-xs text-zinc-500 mb-1">Backing Beat (Background)</p>
                <code className="text-sm text-emerald-400">app/src/main/assets/beat/qbert_02_side_b.wav</code>
              </div>
            </div>
          </div>
        </section>

        {/* README Content */}
        <section className="p-8 rounded-2xl bg-zinc-900 border border-zinc-800">
          <div className="flex items-center gap-3 text-emerald-400 mb-8 pb-6 border-b border-zinc-800">
            <Terminal className="w-6 h-6" />
            <h3 className="text-xl font-bold text-white">Compilation Instructions</h3>
          </div>
          <div className="prose prose-invert prose-emerald max-w-none">
            {readmeContent ? (
              <Markdown>{readmeContent}</Markdown>
            ) : (
              <div className="animate-pulse flex space-x-4">
                <div className="flex-1 space-y-4 py-1">
                  <div className="h-4 bg-zinc-800 rounded w-3/4"></div>
                  <div className="space-y-2">
                    <div className="h-4 bg-zinc-800 rounded"></div>
                    <div className="h-4 bg-zinc-800 rounded w-5/6"></div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </section>

      </main>
    </div>
  );
}
