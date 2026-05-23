"""context_search.py — grep 기반 경량 컨텍스트 검색 (stdlib only, RAG의 1단계).

에이전트가 memory-bank/inputs/usecases/design 전체를 통째로 로드하는 대신, 질의어와
관련된 줄만 끌어와 컨텍스트(=토큰)를 줄이도록 돕는다. 임베딩 기반 검색은 다음 단계(P1).

사용:
    python3 tools/context_search.py "동시 접속"
    python3 tools/context_search.py "latency" --max 30

출력: 매칭된 파일 경로 + 줄번호 + 줄 내용. 외부 의존성 없음.
"""

from __future__ import annotations

import argparse
import os
import sys

SEARCH_DIRS = ("memory-bank", "inputs", "usecases", "design", "workflows")
TEXT_EXTS = (".md", ".yaml", ".yml", ".json", ".txt")


def iter_files(root: str):
    for base in SEARCH_DIRS:
        base_path = os.path.join(root, base)
        if not os.path.isdir(base_path):
            continue
        for dirpath, _dirs, files in os.walk(base_path):
            for name in sorted(files):
                if name.endswith(TEXT_EXTS):
                    yield os.path.join(dirpath, name)


def search(root: str, query: str, max_hits: int) -> int:
    needle = query.lower()
    hits = 0
    for path in iter_files(root):
        try:
            with open(path, encoding="utf-8") as fh:
                for lineno, line in enumerate(fh, 1):
                    if needle in line.lower():
                        rel = os.path.relpath(path, root)
                        print(f"{rel}:{lineno}: {line.rstrip()}")
                        hits += 1
                        if hits >= max_hits:
                            print(f"... (truncated at {max_hits} hits)")
                            return hits
        except (UnicodeDecodeError, OSError):
            continue
    return hits


def main() -> int:
    parser = argparse.ArgumentParser(description="grep-based context retrieval for agents")
    parser.add_argument("query", help="search term (case-insensitive)")
    parser.add_argument("--max", type=int, default=50, help="max matching lines")
    parser.add_argument("--root", default=".", help="project root (default: cwd)")
    args = parser.parse_args()

    hits = search(args.root, args.query, args.max)
    if hits == 0:
        print(f"(no matches for {args.query!r} in {', '.join(SEARCH_DIRS)})")
    return 0


if __name__ == "__main__":
    sys.exit(main())
