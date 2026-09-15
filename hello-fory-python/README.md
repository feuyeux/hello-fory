# Hello fory (Python)

This example demonstrates using Apache fory with Python.

## Prerequisites

- Python 3.10+ (pyfory 1.7.2 ships wheels for CPython 3.10–3.14)

## Setup Instructions

### Recommended: Using [uv](https://github.com/astral-sh/uv) (if installed)

```bash
# Install uv if not already installed
brew install uv  # or see uv docs for other OS

# Create and activate a virtual environment
uv venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate

# Install dependencies
uv pip install -r requirements.txt
```

### Or: Standard Python venv + pip

```bash
python3 -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

## Run Instructions

```bash
python main.py
```